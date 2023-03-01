import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import {
  TrackedEntityAttributeValueAuditFormService,
  TrackedEntityAttributeValueAuditFormGroup,
} from './tracked-entity-attribute-value-audit-form.service';
import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';
import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

@Component({
  selector: 'app-tracked-entity-attribute-value-audit-update',
  templateUrl: './tracked-entity-attribute-value-audit-update.component.html',
})
export class TrackedEntityAttributeValueAuditUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit | null = null;
  auditTypeValues = Object.keys(AuditType);

  dataElementsSharedCollection: IDataElement[] = [];
  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];

  editForm: TrackedEntityAttributeValueAuditFormGroup =
    this.trackedEntityAttributeValueAuditFormService.createTrackedEntityAttributeValueAuditFormGroup();

  constructor(
    protected trackedEntityAttributeValueAuditService: TrackedEntityAttributeValueAuditService,
    protected trackedEntityAttributeValueAuditFormService: TrackedEntityAttributeValueAuditFormService,
    protected dataElementService: DataElementService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityAttributeValueAudit }) => {
      this.trackedEntityAttributeValueAudit = trackedEntityAttributeValueAudit;
      if (trackedEntityAttributeValueAudit) {
        this.updateForm(trackedEntityAttributeValueAudit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityAttributeValueAudit = this.trackedEntityAttributeValueAuditFormService.getTrackedEntityAttributeValueAudit(
      this.editForm
    );
    if (trackedEntityAttributeValueAudit.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityAttributeValueAuditService.update(trackedEntityAttributeValueAudit));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityAttributeValueAuditService.create(trackedEntityAttributeValueAudit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityAttributeValueAudit>>): void {
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

  protected updateForm(trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit): void {
    this.trackedEntityAttributeValueAudit = trackedEntityAttributeValueAudit;
    this.trackedEntityAttributeValueAuditFormService.resetForm(this.editForm, trackedEntityAttributeValueAudit);

    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      trackedEntityAttributeValueAudit.attribute
    );
    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        trackedEntityAttributeValueAudit.entityInstance
      );
  }

  protected loadRelationshipsOptions(): void {
    this.dataElementService
      .query()
      .pipe(map((res: HttpResponse<IDataElement[]>) => res.body ?? []))
      .pipe(
        map((dataElements: IDataElement[]) =>
          this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
            dataElements,
            this.trackedEntityAttributeValueAudit?.attribute
          )
        )
      )
      .subscribe((dataElements: IDataElement[]) => (this.dataElementsSharedCollection = dataElements));

    this.trackedEntityInstanceService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityInstance[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityInstances: ITrackedEntityInstance[]) =>
          this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
            trackedEntityInstances,
            this.trackedEntityAttributeValueAudit?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );
  }
}
