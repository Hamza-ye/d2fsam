import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDataValue, NewDataValue } from '../data-value.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDataValue for edit and NewDataValueFormGroupInput for create.
 */
type DataValueFormGroupInput = IDataValue | PartialWithRequiredKeyOf<NewDataValue>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDataValue | NewDataValue> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type DataValueFormRawValue = FormValueOf<IDataValue>;

type NewDataValueFormRawValue = FormValueOf<NewDataValue>;

type DataValueFormDefaults = Pick<NewDataValue, 'id' | 'created' | 'updated' | 'followup' | 'deleted'>;

type DataValueFormGroupContent = {
  id: FormControl<DataValueFormRawValue['id'] | NewDataValue['id']>;
  value: FormControl<DataValueFormRawValue['value']>;
  storedBy: FormControl<DataValueFormRawValue['storedBy']>;
  created: FormControl<DataValueFormRawValue['created']>;
  updated: FormControl<DataValueFormRawValue['updated']>;
  comment: FormControl<DataValueFormRawValue['comment']>;
  followup: FormControl<DataValueFormRawValue['followup']>;
  deleted: FormControl<DataValueFormRawValue['deleted']>;
  auditType: FormControl<DataValueFormRawValue['auditType']>;
  dataElement: FormControl<DataValueFormRawValue['dataElement']>;
  period: FormControl<DataValueFormRawValue['period']>;
  source: FormControl<DataValueFormRawValue['source']>;
};

export type DataValueFormGroup = FormGroup<DataValueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DataValueFormService {
  createDataValueFormGroup(dataValue: DataValueFormGroupInput = { id: null }): DataValueFormGroup {
    const dataValueRawValue = this.convertDataValueToDataValueRawValue({
      ...this.getFormDefaults(),
      ...dataValue,
    });
    return new FormGroup<DataValueFormGroupContent>({
      id: new FormControl(
        { value: dataValueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      value: new FormControl(dataValueRawValue.value, {
        validators: [Validators.maxLength(50000)],
      }),
      storedBy: new FormControl(dataValueRawValue.storedBy),
      created: new FormControl(dataValueRawValue.created),
      updated: new FormControl(dataValueRawValue.updated),
      comment: new FormControl(dataValueRawValue.comment),
      followup: new FormControl(dataValueRawValue.followup),
      deleted: new FormControl(dataValueRawValue.deleted),
      auditType: new FormControl(dataValueRawValue.auditType),
      dataElement: new FormControl(dataValueRawValue.dataElement, {
        validators: [Validators.required],
      }),
      period: new FormControl(dataValueRawValue.period),
      source: new FormControl(dataValueRawValue.source),
    });
  }

  getDataValue(form: DataValueFormGroup): IDataValue | NewDataValue {
    return this.convertDataValueRawValueToDataValue(form.getRawValue() as DataValueFormRawValue | NewDataValueFormRawValue);
  }

  resetForm(form: DataValueFormGroup, dataValue: DataValueFormGroupInput): void {
    const dataValueRawValue = this.convertDataValueToDataValueRawValue({ ...this.getFormDefaults(), ...dataValue });
    form.reset(
      {
        ...dataValueRawValue,
        id: { value: dataValueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DataValueFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      followup: false,
      deleted: false,
    };
  }

  private convertDataValueRawValueToDataValue(rawDataValue: DataValueFormRawValue | NewDataValueFormRawValue): IDataValue | NewDataValue {
    return {
      ...rawDataValue,
      created: dayjs(rawDataValue.created, DATE_TIME_FORMAT),
      updated: dayjs(rawDataValue.updated, DATE_TIME_FORMAT),
    };
  }

  private convertDataValueToDataValueRawValue(
    dataValue: IDataValue | (Partial<NewDataValue> & DataValueFormDefaults)
  ): DataValueFormRawValue | PartialWithRequiredKeyOf<NewDataValueFormRawValue> {
    return {
      ...dataValue,
      created: dataValue.created ? dataValue.created.format(DATE_TIME_FORMAT) : undefined,
      updated: dataValue.updated ? dataValue.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
