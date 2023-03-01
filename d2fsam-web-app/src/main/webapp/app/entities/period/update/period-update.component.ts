import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PeriodFormService, PeriodFormGroup } from './period-form.service';
import { IPeriod } from '../period.model';
import { PeriodService } from '../service/period.service';
import { IPeriodType } from 'app/entities/period-type/period-type.model';
import { PeriodTypeService } from 'app/entities/period-type/service/period-type.service';

@Component({
  selector: 'app-period-update',
  templateUrl: './period-update.component.html',
})
export class PeriodUpdateComponent implements OnInit {
  isSaving = false;
  period: IPeriod | null = null;

  periodTypesSharedCollection: IPeriodType[] = [];

  editForm: PeriodFormGroup = this.periodFormService.createPeriodFormGroup();

  constructor(
    protected periodService: PeriodService,
    protected periodFormService: PeriodFormService,
    protected periodTypeService: PeriodTypeService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePeriodType = (o1: IPeriodType | null, o2: IPeriodType | null): boolean => this.periodTypeService.comparePeriodType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ period }) => {
      this.period = period;
      if (period) {
        this.updateForm(period);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const period = this.periodFormService.getPeriod(this.editForm);
    if (period.id !== null) {
      this.subscribeToSaveResponse(this.periodService.update(period));
    } else {
      this.subscribeToSaveResponse(this.periodService.create(period));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPeriod>>): void {
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

  protected updateForm(period: IPeriod): void {
    this.period = period;
    this.periodFormService.resetForm(this.editForm, period);

    this.periodTypesSharedCollection = this.periodTypeService.addPeriodTypeToCollectionIfMissing<IPeriodType>(
      this.periodTypesSharedCollection,
      period.periodType
    );
  }

  protected loadRelationshipsOptions(): void {
    this.periodTypeService
      .query()
      .pipe(map((res: HttpResponse<IPeriodType[]>) => res.body ?? []))
      .pipe(
        map((periodTypes: IPeriodType[]) =>
          this.periodTypeService.addPeriodTypeToCollectionIfMissing<IPeriodType>(periodTypes, this.period?.periodType)
        )
      )
      .subscribe((periodTypes: IPeriodType[]) => (this.periodTypesSharedCollection = periodTypes));
  }
}
