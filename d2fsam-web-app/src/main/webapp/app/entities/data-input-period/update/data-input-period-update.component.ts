import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataInputPeriodFormService, DataInputPeriodFormGroup } from './data-input-period-form.service';
import { IDataInputPeriod } from '../data-input-period.model';
import { DataInputPeriodService } from '../service/data-input-period.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';

@Component({
  selector: 'app-data-input-period-update',
  templateUrl: './data-input-period-update.component.html',
})
export class DataInputPeriodUpdateComponent implements OnInit {
  isSaving = false;
  dataInputPeriod: IDataInputPeriod | null = null;

  periodsSharedCollection: IPeriod[] = [];

  editForm: DataInputPeriodFormGroup = this.dataInputPeriodFormService.createDataInputPeriodFormGroup();

  constructor(
    protected dataInputPeriodService: DataInputPeriodService,
    protected dataInputPeriodFormService: DataInputPeriodFormService,
    protected periodService: PeriodService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dataInputPeriod }) => {
      this.dataInputPeriod = dataInputPeriod;
      if (dataInputPeriod) {
        this.updateForm(dataInputPeriod);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dataInputPeriod = this.dataInputPeriodFormService.getDataInputPeriod(this.editForm);
    if (dataInputPeriod.id !== null) {
      this.subscribeToSaveResponse(this.dataInputPeriodService.update(dataInputPeriod));
    } else {
      this.subscribeToSaveResponse(this.dataInputPeriodService.create(dataInputPeriod));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDataInputPeriod>>): void {
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

  protected updateForm(dataInputPeriod: IDataInputPeriod): void {
    this.dataInputPeriod = dataInputPeriod;
    this.dataInputPeriodFormService.resetForm(this.editForm, dataInputPeriod);

    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      dataInputPeriod.period
    );
  }

  protected loadRelationshipsOptions(): void {
    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.dataInputPeriod?.period)))
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));
  }
}
