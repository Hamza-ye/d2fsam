import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TrackedEntityProgramOwnerFormService, TrackedEntityProgramOwnerFormGroup } from './tracked-entity-program-owner-form.service';
import { ITrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';
import { TrackedEntityProgramOwnerService } from '../service/tracked-entity-program-owner.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

@Component({
  selector: 'app-tracked-entity-program-owner-update',
  templateUrl: './tracked-entity-program-owner-update.component.html',
})
export class TrackedEntityProgramOwnerUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityProgramOwner: ITrackedEntityProgramOwner | null = null;

  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];
  programsSharedCollection: IProgram[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];

  editForm: TrackedEntityProgramOwnerFormGroup = this.trackedEntityProgramOwnerFormService.createTrackedEntityProgramOwnerFormGroup();

  constructor(
    protected trackedEntityProgramOwnerService: TrackedEntityProgramOwnerService,
    protected trackedEntityProgramOwnerFormService: TrackedEntityProgramOwnerFormService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected programService: ProgramService,
    protected organisationUnitService: OrganisationUnitService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityProgramOwner }) => {
      this.trackedEntityProgramOwner = trackedEntityProgramOwner;
      if (trackedEntityProgramOwner) {
        this.updateForm(trackedEntityProgramOwner);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityProgramOwner = this.trackedEntityProgramOwnerFormService.getTrackedEntityProgramOwner(this.editForm);
    if (trackedEntityProgramOwner.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityProgramOwnerService.update(trackedEntityProgramOwner));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityProgramOwnerService.create(trackedEntityProgramOwner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityProgramOwner>>): void {
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

  protected updateForm(trackedEntityProgramOwner: ITrackedEntityProgramOwner): void {
    this.trackedEntityProgramOwner = trackedEntityProgramOwner;
    this.trackedEntityProgramOwnerFormService.resetForm(this.editForm, trackedEntityProgramOwner);

    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        trackedEntityProgramOwner.entityInstance
      );
    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      trackedEntityProgramOwner.program
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      trackedEntityProgramOwner.organisationUnit
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trackedEntityInstanceService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityInstance[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityInstances: ITrackedEntityInstance[]) =>
          this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
            trackedEntityInstances,
            this.trackedEntityProgramOwner?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );

    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.trackedEntityProgramOwner?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.trackedEntityProgramOwner?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));
  }
}
