import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOptionSet, NewOptionSet } from '../option-set.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOptionSet for edit and NewOptionSetFormGroupInput for create.
 */
type OptionSetFormGroupInput = IOptionSet | PartialWithRequiredKeyOf<NewOptionSet>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOptionSet | NewOptionSet> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type OptionSetFormRawValue = FormValueOf<IOptionSet>;

type NewOptionSetFormRawValue = FormValueOf<NewOptionSet>;

type OptionSetFormDefaults = Pick<NewOptionSet, 'id' | 'created' | 'updated'>;

type OptionSetFormGroupContent = {
  id: FormControl<OptionSetFormRawValue['id'] | NewOptionSet['id']>;
  uid: FormControl<OptionSetFormRawValue['uid']>;
  code: FormControl<OptionSetFormRawValue['code']>;
  name: FormControl<OptionSetFormRawValue['name']>;
  created: FormControl<OptionSetFormRawValue['created']>;
  updated: FormControl<OptionSetFormRawValue['updated']>;
  valueType: FormControl<OptionSetFormRawValue['valueType']>;
  version: FormControl<OptionSetFormRawValue['version']>;
  createdBy: FormControl<OptionSetFormRawValue['createdBy']>;
  updatedBy: FormControl<OptionSetFormRawValue['updatedBy']>;
};

export type OptionSetFormGroup = FormGroup<OptionSetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OptionSetFormService {
  createOptionSetFormGroup(optionSet: OptionSetFormGroupInput = { id: null }): OptionSetFormGroup {
    const optionSetRawValue = this.convertOptionSetToOptionSetRawValue({
      ...this.getFormDefaults(),
      ...optionSet,
    });
    return new FormGroup<OptionSetFormGroupContent>({
      id: new FormControl(
        { value: optionSetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(optionSetRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(optionSetRawValue.code),
      name: new FormControl(optionSetRawValue.name, {
        validators: [Validators.required],
      }),
      created: new FormControl(optionSetRawValue.created),
      updated: new FormControl(optionSetRawValue.updated),
      valueType: new FormControl(optionSetRawValue.valueType),
      version: new FormControl(optionSetRawValue.version),
      createdBy: new FormControl(optionSetRawValue.createdBy),
      updatedBy: new FormControl(optionSetRawValue.updatedBy),
    });
  }

  getOptionSet(form: OptionSetFormGroup): IOptionSet | NewOptionSet {
    return this.convertOptionSetRawValueToOptionSet(form.getRawValue() as OptionSetFormRawValue | NewOptionSetFormRawValue);
  }

  resetForm(form: OptionSetFormGroup, optionSet: OptionSetFormGroupInput): void {
    const optionSetRawValue = this.convertOptionSetToOptionSetRawValue({ ...this.getFormDefaults(), ...optionSet });
    form.reset(
      {
        ...optionSetRawValue,
        id: { value: optionSetRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OptionSetFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertOptionSetRawValueToOptionSet(rawOptionSet: OptionSetFormRawValue | NewOptionSetFormRawValue): IOptionSet | NewOptionSet {
    return {
      ...rawOptionSet,
      created: dayjs(rawOptionSet.created, DATE_TIME_FORMAT),
      updated: dayjs(rawOptionSet.updated, DATE_TIME_FORMAT),
    };
  }

  private convertOptionSetToOptionSetRawValue(
    optionSet: IOptionSet | (Partial<NewOptionSet> & OptionSetFormDefaults)
  ): OptionSetFormRawValue | PartialWithRequiredKeyOf<NewOptionSetFormRawValue> {
    return {
      ...optionSet,
      created: optionSet.created ? optionSet.created.format(DATE_TIME_FORMAT) : undefined,
      updated: optionSet.updated ? optionSet.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
