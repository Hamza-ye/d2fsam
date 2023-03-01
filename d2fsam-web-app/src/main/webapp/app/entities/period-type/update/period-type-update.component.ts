import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { PeriodTypeFormService, PeriodTypeFormGroup } from './period-type-form.service';
import { IPeriodType } from '../period-type.model';
import { PeriodTypeService } from '../service/period-type.service';

@Component({
  selector: 'app-period-type-update',
  templateUrl: './period-type-update.component.html',
})
export class PeriodTypeUpdateComponent implements OnInit {
  isSaving = false;
  periodType: IPeriodType | null = null;

  editForm: PeriodTypeFormGroup = this.periodTypeFormService.createPeriodTypeFormGroup();

  constructor(
    protected periodTypeService: PeriodTypeService,
    protected periodTypeFormService: PeriodTypeFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ periodType }) => {
      this.periodType = periodType;
      if (periodType) {
        this.updateForm(periodType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const periodType = this.periodTypeFormService.getPeriodType(this.editForm);
    if (periodType.id !== null) {
      this.subscribeToSaveResponse(this.periodTypeService.update(periodType));
    } else {
      this.subscribeToSaveResponse(this.periodTypeService.create(periodType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPeriodType>>): void {
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

  protected updateForm(periodType: IPeriodType): void {
    this.periodType = periodType;
    this.periodTypeFormService.resetForm(this.editForm, periodType);
  }
}
