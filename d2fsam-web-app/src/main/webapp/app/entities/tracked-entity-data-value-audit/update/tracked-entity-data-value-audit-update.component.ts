import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import {
  TrackedEntityDataValueAuditFormService,
  TrackedEntityDataValueAuditFormGroup,
} from './tracked-entity-data-value-audit-form.service';
import { ITrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';
import { TrackedEntityDataValueAuditService } from '../service/tracked-entity-data-value-audit.service';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';
import { ProgramStageInstanceService } from 'app/entities/program-stage-instance/service/program-stage-instance.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

@Component({
  selector: 'app-tracked-entity-data-value-audit-update',
  templateUrl: './tracked-entity-data-value-audit-update.component.html',
})
export class TrackedEntityDataValueAuditUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityDataValueAudit: ITrackedEntityDataValueAudit | null = null;
  auditTypeValues = Object.keys(AuditType);

  programStageInstancesSharedCollection: IProgramStageInstance[] = [];
  dataElementsSharedCollection: IDataElement[] = [];
  periodsSharedCollection: IPeriod[] = [];

  editForm: TrackedEntityDataValueAuditFormGroup = this.trackedEntityDataValueAuditFormService.createTrackedEntityDataValueAuditFormGroup();

  constructor(
    protected trackedEntityDataValueAuditService: TrackedEntityDataValueAuditService,
    protected trackedEntityDataValueAuditFormService: TrackedEntityDataValueAuditFormService,
    protected programStageInstanceService: ProgramStageInstanceService,
    protected dataElementService: DataElementService,
    protected periodService: PeriodService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgramStageInstance = (o1: IProgramStageInstance | null, o2: IProgramStageInstance | null): boolean =>
    this.programStageInstanceService.compareProgramStageInstance(o1, o2);

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityDataValueAudit }) => {
      this.trackedEntityDataValueAudit = trackedEntityDataValueAudit;
      if (trackedEntityDataValueAudit) {
        this.updateForm(trackedEntityDataValueAudit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityDataValueAudit = this.trackedEntityDataValueAuditFormService.getTrackedEntityDataValueAudit(this.editForm);
    if (trackedEntityDataValueAudit.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityDataValueAuditService.update(trackedEntityDataValueAudit));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityDataValueAuditService.create(trackedEntityDataValueAudit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityDataValueAudit>>): void {
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

  protected updateForm(trackedEntityDataValueAudit: ITrackedEntityDataValueAudit): void {
    this.trackedEntityDataValueAudit = trackedEntityDataValueAudit;
    this.trackedEntityDataValueAuditFormService.resetForm(this.editForm, trackedEntityDataValueAudit);

    this.programStageInstancesSharedCollection =
      this.programStageInstanceService.addProgramStageInstanceToCollectionIfMissing<IProgramStageInstance>(
        this.programStageInstancesSharedCollection,
        trackedEntityDataValueAudit.programStageInstance
      );
    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      trackedEntityDataValueAudit.dataElement
    );
    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      trackedEntityDataValueAudit.period
    );
  }

  protected loadRelationshipsOptions(): void {
    this.programStageInstanceService
      .query()
      .pipe(map((res: HttpResponse<IProgramStageInstance[]>) => res.body ?? []))
      .pipe(
        map((programStageInstances: IProgramStageInstance[]) =>
          this.programStageInstanceService.addProgramStageInstanceToCollectionIfMissing<IProgramStageInstance>(
            programStageInstances,
            this.trackedEntityDataValueAudit?.programStageInstance
          )
        )
      )
      .subscribe((programStageInstances: IProgramStageInstance[]) => (this.programStageInstancesSharedCollection = programStageInstances));

    this.dataElementService
      .query()
      .pipe(map((res: HttpResponse<IDataElement[]>) => res.body ?? []))
      .pipe(
        map((dataElements: IDataElement[]) =>
          this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
            dataElements,
            this.trackedEntityDataValueAudit?.dataElement
          )
        )
      )
      .subscribe((dataElements: IDataElement[]) => (this.dataElementsSharedCollection = dataElements));

    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(
        map((periods: IPeriod[]) =>
          this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.trackedEntityDataValueAudit?.period)
        )
      )
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));
  }
}
