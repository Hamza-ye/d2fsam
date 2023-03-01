import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserDataFormService, UserDataFormGroup } from './user-data-form.service';
import { IUserData } from '../user-data.model';
import { UserDataService } from '../service/user-data.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IUserAuthorityGroup } from 'app/entities/user-authority-group/user-authority-group.model';
import { UserAuthorityGroupService } from 'app/entities/user-authority-group/service/user-authority-group.service';

@Component({
  selector: 'app-user-data-update',
  templateUrl: './user-data-update.component.html',
})
export class UserDataUpdateComponent implements OnInit {
  isSaving = false;
  userData: IUserData | null = null;

  usersSharedCollection: IUser[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  userAuthorityGroupsSharedCollection: IUserAuthorityGroup[] = [];

  editForm: UserDataFormGroup = this.userDataFormService.createUserDataFormGroup();

  constructor(
    protected userDataService: UserDataService,
    protected userDataFormService: UserDataFormService,
    protected userService: UserService,
    protected organisationUnitService: OrganisationUnitService,
    protected userAuthorityGroupService: UserAuthorityGroupService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareUserAuthorityGroup = (o1: IUserAuthorityGroup | null, o2: IUserAuthorityGroup | null): boolean =>
    this.userAuthorityGroupService.compareUserAuthorityGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userData }) => {
      this.userData = userData;
      if (userData) {
        this.updateForm(userData);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userData = this.userDataFormService.getUserData(this.editForm);
    if (userData.id !== null) {
      this.subscribeToSaveResponse(this.userDataService.update(userData));
    } else {
      this.subscribeToSaveResponse(this.userDataService.create(userData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserData>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(userData: IUserData): void {
    this.userData = userData;
    this.userDataFormService.resetForm(this.editForm, userData);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      userData.createdBy,
      userData.updatedBy
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      ...(userData.organisationUnits ?? []),
      ...(userData.teiSearchOrganisationUnits ?? []),
      ...(userData.dataViewOrganisationUnits ?? [])
    );
    this.userAuthorityGroupsSharedCollection =
      this.userAuthorityGroupService.addUserAuthorityGroupToCollectionIfMissing<IUserAuthorityGroup>(
        this.userAuthorityGroupsSharedCollection,
        ...(userData.userAuthorityGroups ?? [])
      );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.userData?.createdBy, this.userData?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            ...(this.userData?.organisationUnits ?? []),
            ...(this.userData?.teiSearchOrganisationUnits ?? []),
            ...(this.userData?.dataViewOrganisationUnits ?? [])
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.userAuthorityGroupService
      .query()
      .pipe(map((res: HttpResponse<IUserAuthorityGroup[]>) => res.body ?? []))
      .pipe(
        map((userAuthorityGroups: IUserAuthorityGroup[]) =>
          this.userAuthorityGroupService.addUserAuthorityGroupToCollectionIfMissing<IUserAuthorityGroup>(
            userAuthorityGroups,
            ...(this.userData?.userAuthorityGroups ?? [])
          )
        )
      )
      .subscribe((userAuthorityGroups: IUserAuthorityGroup[]) => (this.userAuthorityGroupsSharedCollection = userAuthorityGroups));
  }
}
