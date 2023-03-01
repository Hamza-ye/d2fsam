import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OrganisationUnitGroupSetFormService, OrganisationUnitGroupSetFormGroup } from './organisation-unit-group-set-form.service';
import { IOrganisationUnitGroupSet } from '../organisation-unit-group-set.model';
import { OrganisationUnitGroupSetService } from '../service/organisation-unit-group-set.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnitGroup } from 'app/entities/organisation-unit-group/organisation-unit-group.model';
import { OrganisationUnitGroupService } from 'app/entities/organisation-unit-group/service/organisation-unit-group.service';

@Component({
  selector: 'app-organisation-unit-group-set-update',
  templateUrl: './organisation-unit-group-set-update.component.html',
})
export class OrganisationUnitGroupSetUpdateComponent implements OnInit {
  isSaving = false;
  organisationUnitGroupSet: IOrganisationUnitGroupSet | null = null;

  usersSharedCollection: IUser[] = [];
  organisationUnitGroupsSharedCollection: IOrganisationUnitGroup[] = [];

  editForm: OrganisationUnitGroupSetFormGroup = this.organisationUnitGroupSetFormService.createOrganisationUnitGroupSetFormGroup();

  constructor(
    protected organisationUnitGroupSetService: OrganisationUnitGroupSetService,
    protected organisationUnitGroupSetFormService: OrganisationUnitGroupSetFormService,
    protected userService: UserService,
    protected organisationUnitGroupService: OrganisationUnitGroupService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrganisationUnitGroup = (o1: IOrganisationUnitGroup | null, o2: IOrganisationUnitGroup | null): boolean =>
    this.organisationUnitGroupService.compareOrganisationUnitGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnitGroupSet }) => {
      this.organisationUnitGroupSet = organisationUnitGroupSet;
      if (organisationUnitGroupSet) {
        this.updateForm(organisationUnitGroupSet);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const organisationUnitGroupSet = this.organisationUnitGroupSetFormService.getOrganisationUnitGroupSet(this.editForm);
    if (organisationUnitGroupSet.id !== null) {
      this.subscribeToSaveResponse(this.organisationUnitGroupSetService.update(organisationUnitGroupSet));
    } else {
      this.subscribeToSaveResponse(this.organisationUnitGroupSetService.create(organisationUnitGroupSet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrganisationUnitGroupSet>>): void {
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

  protected updateForm(organisationUnitGroupSet: IOrganisationUnitGroupSet): void {
    this.organisationUnitGroupSet = organisationUnitGroupSet;
    this.organisationUnitGroupSetFormService.resetForm(this.editForm, organisationUnitGroupSet);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      organisationUnitGroupSet.createdBy,
      organisationUnitGroupSet.updatedBy
    );
    this.organisationUnitGroupsSharedCollection =
      this.organisationUnitGroupService.addOrganisationUnitGroupToCollectionIfMissing<IOrganisationUnitGroup>(
        this.organisationUnitGroupsSharedCollection,
        ...(organisationUnitGroupSet.organisationUnitGroups ?? [])
      );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.organisationUnitGroupSet?.createdBy,
            this.organisationUnitGroupSet?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.organisationUnitGroupService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnitGroup[]>) => res.body ?? []))
      .pipe(
        map((organisationUnitGroups: IOrganisationUnitGroup[]) =>
          this.organisationUnitGroupService.addOrganisationUnitGroupToCollectionIfMissing<IOrganisationUnitGroup>(
            organisationUnitGroups,
            ...(this.organisationUnitGroupSet?.organisationUnitGroups ?? [])
          )
        )
      )
      .subscribe(
        (organisationUnitGroups: IOrganisationUnitGroup[]) => (this.organisationUnitGroupsSharedCollection = organisationUnitGroups)
      );
  }
}
