import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPeriodType, NewPeriodType } from '../period-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPeriodType for edit and NewPeriodTypeFormGroupInput for create.
 */
type PeriodTypeFormGroupInput = IPeriodType | PartialWithRequiredKeyOf<NewPeriodType>;

type PeriodTypeFormDefaults = Pick<NewPeriodType, 'id'>;

type PeriodTypeFormGroupContent = {
  id: FormControl<IPeriodType['id'] | NewPeriodType['id']>;
  name: FormControl<IPeriodType['name']>;
};

export type PeriodTypeFormGroup = FormGroup<PeriodTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PeriodTypeFormService {
  createPeriodTypeFormGroup(periodType: PeriodTypeFormGroupInput = { id: null }): PeriodTypeFormGroup {
    const periodTypeRawValue = {
      ...this.getFormDefaults(),
      ...periodType,
    };
    return new FormGroup<PeriodTypeFormGroupContent>({
      id: new FormControl(
        { value: periodTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(periodTypeRawValue.name),
    });
  }

  getPeriodType(form: PeriodTypeFormGroup): IPeriodType | NewPeriodType {
    return form.getRawValue() as IPeriodType | NewPeriodType;
  }

  resetForm(form: PeriodTypeFormGroup, periodType: PeriodTypeFormGroupInput): void {
    const periodTypeRawValue = { ...this.getFormDefaults(), ...periodType };
    form.reset(
      {
        ...periodTypeRawValue,
        id: { value: periodTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PeriodTypeFormDefaults {
    return {
      id: null,
    };
  }
}
