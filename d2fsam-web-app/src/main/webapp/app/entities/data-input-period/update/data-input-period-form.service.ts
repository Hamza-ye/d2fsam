import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDataInputPeriod, NewDataInputPeriod } from '../data-input-period.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDataInputPeriod for edit and NewDataInputPeriodFormGroupInput for create.
 */
type DataInputPeriodFormGroupInput = IDataInputPeriod | PartialWithRequiredKeyOf<NewDataInputPeriod>;

type DataInputPeriodFormDefaults = Pick<NewDataInputPeriod, 'id'>;

type DataInputPeriodFormGroupContent = {
  id: FormControl<IDataInputPeriod['id'] | NewDataInputPeriod['id']>;
  openingDate: FormControl<IDataInputPeriod['openingDate']>;
  closingDate: FormControl<IDataInputPeriod['closingDate']>;
  period: FormControl<IDataInputPeriod['period']>;
};

export type DataInputPeriodFormGroup = FormGroup<DataInputPeriodFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DataInputPeriodFormService {
  createDataInputPeriodFormGroup(dataInputPeriod: DataInputPeriodFormGroupInput = { id: null }): DataInputPeriodFormGroup {
    const dataInputPeriodRawValue = {
      ...this.getFormDefaults(),
      ...dataInputPeriod,
    };
    return new FormGroup<DataInputPeriodFormGroupContent>({
      id: new FormControl(
        { value: dataInputPeriodRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      openingDate: new FormControl(dataInputPeriodRawValue.openingDate),
      closingDate: new FormControl(dataInputPeriodRawValue.closingDate),
      period: new FormControl(dataInputPeriodRawValue.period),
    });
  }

  getDataInputPeriod(form: DataInputPeriodFormGroup): IDataInputPeriod | NewDataInputPeriod {
    return form.getRawValue() as IDataInputPeriod | NewDataInputPeriod;
  }

  resetForm(form: DataInputPeriodFormGroup, dataInputPeriod: DataInputPeriodFormGroupInput): void {
    const dataInputPeriodRawValue = { ...this.getFormDefaults(), ...dataInputPeriod };
    form.reset(
      {
        ...dataInputPeriodRawValue,
        id: { value: dataInputPeriodRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DataInputPeriodFormDefaults {
    return {
      id: null,
    };
  }
}
