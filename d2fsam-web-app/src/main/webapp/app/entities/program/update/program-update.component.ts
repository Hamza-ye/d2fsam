import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramFormService, ProgramFormGroup } from './program-form.service';
import { IProgram } from '../program.model';
import { ProgramService } from '../service/program.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IPeriodType } from 'app/entities/period-type/period-type.model';
import { PeriodTypeService } from 'app/entities/period-type/service/period-type.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { ProgramType } from 'app/entities/enumerations/program-type.model';
import { AccessLevel } from 'app/entities/enumerations/access-level.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

@Component({
  selector: 'app-program-update',
  templateUrl: './program-update.component.html',
})
export class ProgramUpdateComponent implements OnInit {
  isSaving = false;
  program: IProgram | null = null;
  programTypeValues = Object.keys(ProgramType);
  accessLevelValues = Object.keys(AccessLevel);
  featureTypeValues = Object.keys(FeatureType);

  periodsSharedCollection: IPeriod[] = [];
  periodTypesSharedCollection: IPeriodType[] = [];
  programsSharedCollection: IProgram[] = [];
  trackedEntityTypesSharedCollection: ITrackedEntityType[] = [];
  usersSharedCollection: IUser[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];

  editForm: ProgramFormGroup = this.programFormService.createProgramFormGroup();

  constructor(
    protected programService: ProgramService,
    protected programFormService: ProgramFormService,
    protected periodService: PeriodService,
    protected periodTypeService: PeriodTypeService,
    protected trackedEntityTypeService: TrackedEntityTypeService,
    protected userService: UserService,
    protected organisationUnitService: OrganisationUnitService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  comparePeriodType = (o1: IPeriodType | null, o2: IPeriodType | null): boolean => this.periodTypeService.comparePeriodType(o1, o2);

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareTrackedEntityType = (o1: ITrackedEntityType | null, o2: ITrackedEntityType | null): boolean =>
    this.trackedEntityTypeService.compareTrackedEntityType(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ program }) => {
      this.program = program;
      if (program) {
        this.updateForm(program);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const program = this.programFormService.getProgram(this.editForm);
    if (program.id !== null) {
      this.subscribeToSaveResponse(this.programService.update(program));
    } else {
      this.subscribeToSaveResponse(this.programService.create(program));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgram>>): void {
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

  protected updateForm(program: IProgram): void {
    this.program = program;
    this.programFormService.resetForm(this.editForm, program);

    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(this.periodsSharedCollection, program.period);
    this.periodTypesSharedCollection = this.periodTypeService.addPeriodTypeToCollectionIfMissing<IPeriodType>(
      this.periodTypesSharedCollection,
      program.expiryPeriodType
    );
    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      program.relatedProgram
    );
    this.trackedEntityTypesSharedCollection = this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
      this.trackedEntityTypesSharedCollection,
      program.trackedEntityType
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      program.createdBy,
      program.updatedBy
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      ...(program.organisationUnits ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.program?.period)))
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));

    this.periodTypeService
      .query()
      .pipe(map((res: HttpResponse<IPeriodType[]>) => res.body ?? []))
      .pipe(
        map((periodTypes: IPeriodType[]) =>
          this.periodTypeService.addPeriodTypeToCollectionIfMissing<IPeriodType>(periodTypes, this.program?.expiryPeriodType)
        )
      )
      .subscribe((periodTypes: IPeriodType[]) => (this.periodTypesSharedCollection = periodTypes));

    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) => this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.program?.relatedProgram))
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.trackedEntityTypeService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityType[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityTypes: ITrackedEntityType[]) =>
          this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
            trackedEntityTypes,
            this.program?.trackedEntityType
          )
        )
      )
      .subscribe((trackedEntityTypes: ITrackedEntityType[]) => (this.trackedEntityTypesSharedCollection = trackedEntityTypes));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.program?.createdBy, this.program?.updatedBy)
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
            ...(this.program?.organisationUnits ?? [])
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));
  }
}
