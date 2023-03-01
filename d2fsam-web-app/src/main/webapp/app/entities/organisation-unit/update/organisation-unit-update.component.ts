import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OrganisationUnitFormService, OrganisationUnitFormGroup } from './organisation-unit-form.service';
import { IOrganisationUnit } from '../organisation-unit.model';
import { OrganisationUnitService } from '../service/organisation-unit.service';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { FileResourceService } from 'app/entities/file-resource/service/file-resource.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IChv } from 'app/entities/chv/chv.model';
import { ChvService } from 'app/entities/chv/service/chv.service';
import { OrganisationUnitType } from 'app/entities/enumerations/organisation-unit-type.model';

@Component({
  selector: 'app-organisation-unit-update',
  templateUrl: './organisation-unit-update.component.html',
})
export class OrganisationUnitUpdateComponent implements OnInit {
  isSaving = false;
  organisationUnit: IOrganisationUnit | null = null;
  organisationUnitTypeValues = Object.keys(OrganisationUnitType);

  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  fileResourcesSharedCollection: IFileResource[] = [];
  usersSharedCollection: IUser[] = [];
  chvsSharedCollection: IChv[] = [];

  editForm: OrganisationUnitFormGroup = this.organisationUnitFormService.createOrganisationUnitFormGroup();

  constructor(
    protected organisationUnitService: OrganisationUnitService,
    protected organisationUnitFormService: OrganisationUnitFormService,
    protected fileResourceService: FileResourceService,
    protected userService: UserService,
    protected chvService: ChvService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareFileResource = (o1: IFileResource | null, o2: IFileResource | null): boolean =>
    this.fileResourceService.compareFileResource(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareChv = (o1: IChv | null, o2: IChv | null): boolean => this.chvService.compareChv(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnit }) => {
      this.organisationUnit = organisationUnit;
      if (organisationUnit) {
        this.updateForm(organisationUnit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const organisationUnit = this.organisationUnitFormService.getOrganisationUnit(this.editForm);
    if (organisationUnit.id !== null) {
      this.subscribeToSaveResponse(this.organisationUnitService.update(organisationUnit));
    } else {
      this.subscribeToSaveResponse(this.organisationUnitService.create(organisationUnit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrganisationUnit>>): void {
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

  protected updateForm(organisationUnit: IOrganisationUnit): void {
    this.organisationUnit = organisationUnit;
    this.organisationUnitFormService.resetForm(this.editForm, organisationUnit);

    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      organisationUnit.parent,
      organisationUnit.hfHomeSubVillage,
      organisationUnit.servicingHf
    );
    this.fileResourcesSharedCollection = this.fileResourceService.addFileResourceToCollectionIfMissing<IFileResource>(
      this.fileResourcesSharedCollection,
      organisationUnit.image
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      organisationUnit.createdBy,
      organisationUnit.updatedBy
    );
    this.chvsSharedCollection = this.chvService.addChvToCollectionIfMissing<IChv>(this.chvsSharedCollection, organisationUnit.assignedChv);
  }

  protected loadRelationshipsOptions(): void {
    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.organisationUnit?.parent,
            this.organisationUnit?.hfHomeSubVillage,
            this.organisationUnit?.servicingHf
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.fileResourceService
      .query()
      .pipe(map((res: HttpResponse<IFileResource[]>) => res.body ?? []))
      .pipe(
        map((fileResources: IFileResource[]) =>
          this.fileResourceService.addFileResourceToCollectionIfMissing<IFileResource>(fileResources, this.organisationUnit?.image)
        )
      )
      .subscribe((fileResources: IFileResource[]) => (this.fileResourcesSharedCollection = fileResources));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.organisationUnit?.createdBy, this.organisationUnit?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.chvService
      .query()
      .pipe(map((res: HttpResponse<IChv[]>) => res.body ?? []))
      .pipe(map((chvs: IChv[]) => this.chvService.addChvToCollectionIfMissing<IChv>(chvs, this.organisationUnit?.assignedChv)))
      .subscribe((chvs: IChv[]) => (this.chvsSharedCollection = chvs));
  }
}
