import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOption, NewOption } from '../option.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOption for edit and NewOptionFormGroupInput for create.
 */
type OptionFormGroupInput = IOption | PartialWithRequiredKeyOf<NewOption>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOption | NewOption> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type OptionFormRawValue = FormValueOf<IOption>;

type NewOptionFormRawValue = FormValueOf<NewOption>;

type OptionFormDefaults = Pick<NewOption, 'id' | 'created' | 'updated'>;

type OptionFormGroupContent = {
  id: FormControl<OptionFormRawValue['id'] | NewOption['id']>;
  uid: FormControl<OptionFormRawValue['uid']>;
  code: FormControl<OptionFormRawValue['code']>;
  name: FormControl<OptionFormRawValue['name']>;
  created: FormControl<OptionFormRawValue['created']>;
  updated: FormControl<OptionFormRawValue['updated']>;
  description: FormControl<OptionFormRawValue['description']>;
  sortOrder: FormControl<OptionFormRawValue['sortOrder']>;
  optionSet: FormControl<OptionFormRawValue['optionSet']>;
  createdBy: FormControl<OptionFormRawValue['createdBy']>;
  updatedBy: FormControl<OptionFormRawValue['updatedBy']>;
};

export type OptionFormGroup = FormGroup<OptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OptionFormService {
  createOptionFormGroup(option: OptionFormGroupInput = { id: null }): OptionFormGroup {
    const optionRawValue = this.convertOptionToOptionRawValue({
      ...this.getFormDefaults(),
      ...option,
    });
    return new FormGroup<OptionFormGroupContent>({
      id: new FormControl(
        { value: optionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(optionRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(optionRawValue.code),
      name: new FormControl(optionRawValue.name, {
        validators: [Validators.required],
      }),
      created: new FormControl(optionRawValue.created),
      updated: new FormControl(optionRawValue.updated),
      description: new FormControl(optionRawValue.description),
      sortOrder: new FormControl(optionRawValue.sortOrder),
      optionSet: new FormControl(optionRawValue.optionSet),
      createdBy: new FormControl(optionRawValue.createdBy),
      updatedBy: new FormControl(optionRawValue.updatedBy),
    });
  }

  getOption(form: OptionFormGroup): IOption | NewOption {
    return this.convertOptionRawValueToOption(form.getRawValue() as OptionFormRawValue | NewOptionFormRawValue);
  }

  resetForm(form: OptionFormGroup, option: OptionFormGroupInput): void {
    const optionRawValue = this.convertOptionToOptionRawValue({ ...this.getFormDefaults(), ...option });
    form.reset(
      {
        ...optionRawValue,
        id: { value: optionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OptionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertOptionRawValueToOption(rawOption: OptionFormRawValue | NewOptionFormRawValue): IOption | NewOption {
    return {
      ...rawOption,
      created: dayjs(rawOption.created, DATE_TIME_FORMAT),
      updated: dayjs(rawOption.updated, DATE_TIME_FORMAT),
    };
  }

  private convertOptionToOptionRawValue(
    option: IOption | (Partial<NewOption> & OptionFormDefaults)
  ): OptionFormRawValue | PartialWithRequiredKeyOf<NewOptionFormRawValue> {
    return {
      ...option,
      created: option.created ? option.created.format(DATE_TIME_FORMAT) : undefined,
      updated: option.updated ? option.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
