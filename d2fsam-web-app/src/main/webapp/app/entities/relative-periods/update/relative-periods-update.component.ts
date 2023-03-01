import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { RelativePeriodsFormService, RelativePeriodsFormGroup } from './relative-periods-form.service';
import { IRelativePeriods } from '../relative-periods.model';
import { RelativePeriodsService } from '../service/relative-periods.service';

@Component({
  selector: 'app-relative-periods-update',
  templateUrl: './relative-periods-update.component.html',
})
export class RelativePeriodsUpdateComponent implements OnInit {
  isSaving = false;
  relativePeriods: IRelativePeriods | null = null;

  editForm: RelativePeriodsFormGroup = this.relativePeriodsFormService.createRelativePeriodsFormGroup();

  constructor(
    protected relativePeriodsService: RelativePeriodsService,
    protected relativePeriodsFormService: RelativePeriodsFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relativePeriods }) => {
      this.relativePeriods = relativePeriods;
      if (relativePeriods) {
        this.updateForm(relativePeriods);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relativePeriods = this.relativePeriodsFormService.getRelativePeriods(this.editForm);
    if (relativePeriods.id !== null) {
      this.subscribeToSaveResponse(this.relativePeriodsService.update(relativePeriods));
    } else {
      this.subscribeToSaveResponse(this.relativePeriodsService.create(relativePeriods));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelativePeriods>>): void {
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

  protected updateForm(relativePeriods: IRelativePeriods): void {
    this.relativePeriods = relativePeriods;
    this.relativePeriodsFormService.resetForm(this.editForm, relativePeriods);
  }
}
