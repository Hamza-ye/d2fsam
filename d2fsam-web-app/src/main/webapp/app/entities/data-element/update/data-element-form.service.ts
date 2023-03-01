import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDataElement, NewDataElement } from '../data-element.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDataElement for edit and NewDataElementFormGroupInput for create.
 */
type DataElementFormGroupInput = IDataElement | PartialWithRequiredKeyOf<NewDataElement>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDataElement | NewDataElement> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type DataElementFormRawValue = FormValueOf<IDataElement>;

type NewDataElementFormRawValue = FormValueOf<NewDataElement>;

type DataElementFormDefaults = Pick<
  NewDataElement,
  | 'id'
  | 'created'
  | 'updated'
  | 'displayInListNoProgram'
  | 'zeroIsSignificant'
  | 'mandatory'
  | 'uniqueAttribute'
  | 'orgunitScope'
  | 'skipSynchronization'
  | 'confidential'
>;

type DataElementFormGroupContent = {
  id: FormControl<DataElementFormRawValue['id'] | NewDataElement['id']>;
  uid: FormControl<DataElementFormRawValue['uid']>;
  code: FormControl<DataElementFormRawValue['code']>;
  created: FormControl<DataElementFormRawValue['created']>;
  updated: FormControl<DataElementFormRawValue['updated']>;
  name: FormControl<DataElementFormRawValue['name']>;
  description: FormControl<DataElementFormRawValue['description']>;
  valueType: FormControl<DataElementFormRawValue['valueType']>;
  aggregationType: FormControl<DataElementFormRawValue['aggregationType']>;
  displayInListNoProgram: FormControl<DataElementFormRawValue['displayInListNoProgram']>;
  zeroIsSignificant: FormControl<DataElementFormRawValue['zeroIsSignificant']>;
  mandatory: FormControl<DataElementFormRawValue['mandatory']>;
  uniqueAttribute: FormControl<DataElementFormRawValue['uniqueAttribute']>;
  fieldMask: FormControl<DataElementFormRawValue['fieldMask']>;
  orgunitScope: FormControl<DataElementFormRawValue['orgunitScope']>;
  skipSynchronization: FormControl<DataElementFormRawValue['skipSynchronization']>;
  confidential: FormControl<DataElementFormRawValue['confidential']>;
  optionSet: FormControl<DataElementFormRawValue['optionSet']>;
  createdBy: FormControl<DataElementFormRawValue['createdBy']>;
  updatedBy: FormControl<DataElementFormRawValue['updatedBy']>;
};

export type DataElementFormGroup = FormGroup<DataElementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DataElementFormService {
  createDataElementFormGroup(dataElement: DataElementFormGroupInput = { id: null }): DataElementFormGroup {
    const dataElementRawValue = this.convertDataElementToDataElementRawValue({
      ...this.getFormDefaults(),
      ...dataElement,
    });
    return new FormGroup<DataElementFormGroupContent>({
      id: new FormControl(
        { value: dataElementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(dataElementRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(dataElementRawValue.code),
      created: new FormControl(dataElementRawValue.created),
      updated: new FormControl(dataElementRawValue.updated),
      name: new FormControl(dataElementRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(dataElementRawValue.description),
      valueType: new FormControl(dataElementRawValue.valueType),
      aggregationType: new FormControl(dataElementRawValue.aggregationType),
      displayInListNoProgram: new FormControl(dataElementRawValue.displayInListNoProgram),
      zeroIsSignificant: new FormControl(dataElementRawValue.zeroIsSignificant),
      mandatory: new FormControl(dataElementRawValue.mandatory),
      uniqueAttribute: new FormControl(dataElementRawValue.uniqueAttribute),
      fieldMask: new FormControl(dataElementRawValue.fieldMask),
      orgunitScope: new FormControl(dataElementRawValue.orgunitScope),
      skipSynchronization: new FormControl(dataElementRawValue.skipSynchronization),
      confidential: new FormControl(dataElementRawValue.confidential),
      optionSet: new FormControl(dataElementRawValue.optionSet),
      createdBy: new FormControl(dataElementRawValue.createdBy),
      updatedBy: new FormControl(dataElementRawValue.updatedBy),
    });
  }

  getDataElement(form: DataElementFormGroup): IDataElement | NewDataElement {
    return this.convertDataElementRawValueToDataElement(form.getRawValue() as DataElementFormRawValue | NewDataElementFormRawValue);
  }

  resetForm(form: DataElementFormGroup, dataElement: DataElementFormGroupInput): void {
    const dataElementRawValue = this.convertDataElementToDataElementRawValue({ ...this.getFormDefaults(), ...dataElement });
    form.reset(
      {
        ...dataElementRawValue,
        id: { value: dataElementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DataElementFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      displayInListNoProgram: false,
      zeroIsSignificant: false,
      mandatory: false,
      uniqueAttribute: false,
      orgunitScope: false,
      skipSynchronization: false,
      confidential: false,
    };
  }

  private convertDataElementRawValueToDataElement(
    rawDataElement: DataElementFormRawValue | NewDataElementFormRawValue
  ): IDataElement | NewDataElement {
    return {
      ...rawDataElement,
      created: dayjs(rawDataElement.created, DATE_TIME_FORMAT),
      updated: dayjs(rawDataElement.updated, DATE_TIME_FORMAT),
    };
  }

  private convertDataElementToDataElementRawValue(
    dataElement: IDataElement | (Partial<NewDataElement> & DataElementFormDefaults)
  ): DataElementFormRawValue | PartialWithRequiredKeyOf<NewDataElementFormRawValue> {
    return {
      ...dataElement,
      created: dataElement.created ? dataElement.created.format(DATE_TIME_FORMAT) : undefined,
      updated: dataElement.updated ? dataElement.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
