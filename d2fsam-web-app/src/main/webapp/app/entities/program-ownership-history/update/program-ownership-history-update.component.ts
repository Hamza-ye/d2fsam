import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramOwnershipHistoryFormService, ProgramOwnershipHistoryFormGroup } from './program-ownership-history-form.service';
import { IProgramOwnershipHistory } from '../program-ownership-history.model';
import { ProgramOwnershipHistoryService } from '../service/program-ownership-history.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

@Component({
  selector: 'app-program-ownership-history-update',
  templateUrl: './program-ownership-history-update.component.html',
})
export class ProgramOwnershipHistoryUpdateComponent implements OnInit {
  isSaving = false;
  programOwnershipHistory: IProgramOwnershipHistory | null = null;

  programsSharedCollection: IProgram[] = [];
  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];

  editForm: ProgramOwnershipHistoryFormGroup = this.programOwnershipHistoryFormService.createProgramOwnershipHistoryFormGroup();

  constructor(
    protected programOwnershipHistoryService: ProgramOwnershipHistoryService,
    protected programOwnershipHistoryFormService: ProgramOwnershipHistoryFormService,
    protected programService: ProgramService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected organisationUnitService: OrganisationUnitService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programOwnershipHistory }) => {
      this.programOwnershipHistory = programOwnershipHistory;
      if (programOwnershipHistory) {
        this.updateForm(programOwnershipHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programOwnershipHistory = this.programOwnershipHistoryFormService.getProgramOwnershipHistory(this.editForm);
    if (programOwnershipHistory.id !== null) {
      this.subscribeToSaveResponse(this.programOwnershipHistoryService.update(programOwnershipHistory));
    } else {
      this.subscribeToSaveResponse(this.programOwnershipHistoryService.create(programOwnershipHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramOwnershipHistory>>): void {
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

  protected updateForm(programOwnershipHistory: IProgramOwnershipHistory): void {
    this.programOwnershipHistory = programOwnershipHistory;
    this.programOwnershipHistoryFormService.resetForm(this.editForm, programOwnershipHistory);

    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      programOwnershipHistory.program
    );
    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        programOwnershipHistory.entityInstance
      );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      programOwnershipHistory.organisationUnit
    );
  }

  protected loadRelationshipsOptions(): void {
    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.programOwnershipHistory?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.trackedEntityInstanceService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityInstance[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityInstances: ITrackedEntityInstance[]) =>
          this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
            trackedEntityInstances,
            this.programOwnershipHistory?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.programOwnershipHistory?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));
  }
}
