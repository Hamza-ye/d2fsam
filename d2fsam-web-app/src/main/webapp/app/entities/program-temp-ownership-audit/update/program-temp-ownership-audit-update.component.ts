import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramTempOwnershipAuditFormService, ProgramTempOwnershipAuditFormGroup } from './program-temp-ownership-audit-form.service';
import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';
import { ProgramTempOwnershipAuditService } from '../service/program-temp-ownership-audit.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';

@Component({
  selector: 'app-program-temp-ownership-audit-update',
  templateUrl: './program-temp-ownership-audit-update.component.html',
})
export class ProgramTempOwnershipAuditUpdateComponent implements OnInit {
  isSaving = false;
  programTempOwnershipAudit: IProgramTempOwnershipAudit | null = null;

  programsSharedCollection: IProgram[] = [];
  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];

  editForm: ProgramTempOwnershipAuditFormGroup = this.programTempOwnershipAuditFormService.createProgramTempOwnershipAuditFormGroup();

  constructor(
    protected programTempOwnershipAuditService: ProgramTempOwnershipAuditService,
    protected programTempOwnershipAuditFormService: ProgramTempOwnershipAuditFormService,
    protected programService: ProgramService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programTempOwnershipAudit }) => {
      this.programTempOwnershipAudit = programTempOwnershipAudit;
      if (programTempOwnershipAudit) {
        this.updateForm(programTempOwnershipAudit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programTempOwnershipAudit = this.programTempOwnershipAuditFormService.getProgramTempOwnershipAudit(this.editForm);
    if (programTempOwnershipAudit.id !== null) {
      this.subscribeToSaveResponse(this.programTempOwnershipAuditService.update(programTempOwnershipAudit));
    } else {
      this.subscribeToSaveResponse(this.programTempOwnershipAuditService.create(programTempOwnershipAudit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramTempOwnershipAudit>>): void {
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

  protected updateForm(programTempOwnershipAudit: IProgramTempOwnershipAudit): void {
    this.programTempOwnershipAudit = programTempOwnershipAudit;
    this.programTempOwnershipAuditFormService.resetForm(this.editForm, programTempOwnershipAudit);

    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      programTempOwnershipAudit.program
    );
    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        programTempOwnershipAudit.entityInstance
      );
  }

  protected loadRelationshipsOptions(): void {
    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.programTempOwnershipAudit?.program)
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
            this.programTempOwnershipAudit?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );
  }
}
