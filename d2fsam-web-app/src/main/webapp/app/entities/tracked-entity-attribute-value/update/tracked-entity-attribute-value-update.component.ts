import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import {
  TrackedEntityAttributeValueFormService,
  TrackedEntityAttributeValueFormGroup,
} from './tracked-entity-attribute-value-form.service';
import { ITrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';
import { TrackedEntityAttributeValueService } from '../service/tracked-entity-attribute-value.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';

@Component({
  selector: 'app-tracked-entity-attribute-value-update',
  templateUrl: './tracked-entity-attribute-value-update.component.html',
})
export class TrackedEntityAttributeValueUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityAttributeValue: ITrackedEntityAttributeValue | null = null;

  dataElementsSharedCollection: IDataElement[] = [];
  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];

  editForm: TrackedEntityAttributeValueFormGroup = this.trackedEntityAttributeValueFormService.createTrackedEntityAttributeValueFormGroup();

  constructor(
    protected trackedEntityAttributeValueService: TrackedEntityAttributeValueService,
    protected trackedEntityAttributeValueFormService: TrackedEntityAttributeValueFormService,
    protected dataElementService: DataElementService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityAttributeValue }) => {
      this.trackedEntityAttributeValue = trackedEntityAttributeValue;
      if (trackedEntityAttributeValue) {
        this.updateForm(trackedEntityAttributeValue);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityAttributeValue = this.trackedEntityAttributeValueFormService.getTrackedEntityAttributeValue(this.editForm);
    if (trackedEntityAttributeValue.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityAttributeValueService.update(trackedEntityAttributeValue));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityAttributeValueService.create(trackedEntityAttributeValue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityAttributeValue>>): void {
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

  protected updateForm(trackedEntityAttributeValue: ITrackedEntityAttributeValue): void {
    this.trackedEntityAttributeValue = trackedEntityAttributeValue;
    this.trackedEntityAttributeValueFormService.resetForm(this.editForm, trackedEntityAttributeValue);

    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      trackedEntityAttributeValue.attribute
    );
    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        trackedEntityAttributeValue.entityInstance
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
            this.trackedEntityAttributeValue?.attribute
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
            this.trackedEntityAttributeValue?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );
  }
}
