import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OrganisationUnitGroupFormService, OrganisationUnitGroupFormGroup } from './organisation-unit-group-form.service';
import { IOrganisationUnitGroup } from '../organisation-unit-group.model';
import { OrganisationUnitGroupService } from '../service/organisation-unit-group.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

@Component({
  selector: 'app-organisation-unit-group-update',
  templateUrl: './organisation-unit-group-update.component.html',
})
export class OrganisationUnitGroupUpdateComponent implements OnInit {
  isSaving = false;
  organisationUnitGroup: IOrganisationUnitGroup | null = null;

  usersSharedCollection: IUser[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];

  editForm: OrganisationUnitGroupFormGroup = this.organisationUnitGroupFormService.createOrganisationUnitGroupFormGroup();

  constructor(
    protected organisationUnitGroupService: OrganisationUnitGroupService,
    protected organisationUnitGroupFormService: OrganisationUnitGroupFormService,
    protected userService: UserService,
    protected organisationUnitService: OrganisationUnitService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnitGroup }) => {
      this.organisationUnitGroup = organisationUnitGroup;
      if (organisationUnitGroup) {
        this.updateForm(organisationUnitGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const organisationUnitGroup = this.organisationUnitGroupFormService.getOrganisationUnitGroup(this.editForm);
    if (organisationUnitGroup.id !== null) {
      this.subscribeToSaveResponse(this.organisationUnitGroupService.update(organisationUnitGroup));
    } else {
      this.subscribeToSaveResponse(this.organisationUnitGroupService.create(organisationUnitGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrganisationUnitGroup>>): void {
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

  protected updateForm(organisationUnitGroup: IOrganisationUnitGroup): void {
    this.organisationUnitGroup = organisationUnitGroup;
    this.organisationUnitGroupFormService.resetForm(this.editForm, organisationUnitGroup);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      organisationUnitGroup.createdBy,
      organisationUnitGroup.updatedBy
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      ...(organisationUnitGroup.members ?? [])
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
            this.organisationUnitGroup?.createdBy,
            this.organisationUnitGroup?.updatedBy
          )
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
            ...(this.organisationUnitGroup?.members ?? [])
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));
  }
}
