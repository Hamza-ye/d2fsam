import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TrackedEntityInstanceFormService, TrackedEntityInstanceFormGroup } from './tracked-entity-instance-form.service';
import { ITrackedEntityInstance } from '../tracked-entity-instance.model';
import { TrackedEntityInstanceService } from '../service/tracked-entity-instance.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

@Component({
  selector: 'app-tracked-entity-instance-update',
  templateUrl: './tracked-entity-instance-update.component.html',
})
export class TrackedEntityInstanceUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityInstance: ITrackedEntityInstance | null = null;
  featureTypeValues = Object.keys(FeatureType);

  periodsSharedCollection: IPeriod[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  trackedEntityTypesSharedCollection: ITrackedEntityType[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: TrackedEntityInstanceFormGroup = this.trackedEntityInstanceFormService.createTrackedEntityInstanceFormGroup();

  constructor(
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected trackedEntityInstanceFormService: TrackedEntityInstanceFormService,
    protected periodService: PeriodService,
    protected organisationUnitService: OrganisationUnitService,
    protected trackedEntityTypeService: TrackedEntityTypeService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareTrackedEntityType = (o1: ITrackedEntityType | null, o2: ITrackedEntityType | null): boolean =>
    this.trackedEntityTypeService.compareTrackedEntityType(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityInstance }) => {
      this.trackedEntityInstance = trackedEntityInstance;
      if (trackedEntityInstance) {
        this.updateForm(trackedEntityInstance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityInstance = this.trackedEntityInstanceFormService.getTrackedEntityInstance(this.editForm);
    if (trackedEntityInstance.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityInstanceService.update(trackedEntityInstance));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityInstanceService.create(trackedEntityInstance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityInstance>>): void {
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

  protected updateForm(trackedEntityInstance: ITrackedEntityInstance): void {
    this.trackedEntityInstance = trackedEntityInstance;
    this.trackedEntityInstanceFormService.resetForm(this.editForm, trackedEntityInstance);

    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      trackedEntityInstance.period
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      trackedEntityInstance.organisationUnit
    );
    this.trackedEntityTypesSharedCollection = this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
      this.trackedEntityTypesSharedCollection,
      trackedEntityInstance.trackedEntityType
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      trackedEntityInstance.createdBy,
      trackedEntityInstance.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(
        map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.trackedEntityInstance?.period))
      )
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.trackedEntityInstance?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.trackedEntityTypeService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityType[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityTypes: ITrackedEntityType[]) =>
          this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
            trackedEntityTypes,
            this.trackedEntityInstance?.trackedEntityType
          )
        )
      )
      .subscribe((trackedEntityTypes: ITrackedEntityType[]) => (this.trackedEntityTypesSharedCollection = trackedEntityTypes));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.trackedEntityInstance?.createdBy,
            this.trackedEntityInstance?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
