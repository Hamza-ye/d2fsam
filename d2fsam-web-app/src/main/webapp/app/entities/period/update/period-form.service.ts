import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPeriod, NewPeriod } from '../period.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPeriod for edit and NewPeriodFormGroupInput for create.
 */
type PeriodFormGroupInput = IPeriod | PartialWithRequiredKeyOf<NewPeriod>;

type PeriodFormDefaults = Pick<NewPeriod, 'id'>;

type PeriodFormGroupContent = {
  id: FormControl<IPeriod['id'] | NewPeriod['id']>;
  name: FormControl<IPeriod['name']>;
  startDate: FormControl<IPeriod['startDate']>;
  endDate: FormControl<IPeriod['endDate']>;
  periodType: FormControl<IPeriod['periodType']>;
};

export type PeriodFormGroup = FormGroup<PeriodFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PeriodFormService {
  createPeriodFormGroup(period: PeriodFormGroupInput = { id: null }): PeriodFormGroup {
    const periodRawValue = {
      ...this.getFormDefaults(),
      ...period,
    };
    return new FormGroup<PeriodFormGroupContent>({
      id: new FormControl(
        { value: periodRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(periodRawValue.name),
      startDate: new FormControl(periodRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(periodRawValue.endDate, {
        validators: [Validators.required],
      }),
      periodType: new FormControl(periodRawValue.periodType, {
        validators: [Validators.required],
      }),
    });
  }

  getPeriod(form: PeriodFormGroup): IPeriod | NewPeriod {
    return form.getRawValue() as IPeriod | NewPeriod;
  }

  resetForm(form: PeriodFormGroup, period: PeriodFormGroupInput): void {
    const periodRawValue = { ...this.getFormDefaults(), ...period };
    form.reset(
      {
        ...periodRawValue,
        id: { value: periodRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PeriodFormDefaults {
    return {
      id: null,
    };
  }
}
