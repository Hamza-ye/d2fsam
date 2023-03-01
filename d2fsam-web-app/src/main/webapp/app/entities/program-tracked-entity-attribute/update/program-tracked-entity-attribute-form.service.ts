import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramTrackedEntityAttribute, NewProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramTrackedEntityAttribute for edit and NewProgramTrackedEntityAttributeFormGroupInput for create.
 */
type ProgramTrackedEntityAttributeFormGroupInput =
  | IProgramTrackedEntityAttribute
  | PartialWithRequiredKeyOf<NewProgramTrackedEntityAttribute>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramTrackedEntityAttribute | NewProgramTrackedEntityAttribute> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ProgramTrackedEntityAttributeFormRawValue = FormValueOf<IProgramTrackedEntityAttribute>;

type NewProgramTrackedEntityAttributeFormRawValue = FormValueOf<NewProgramTrackedEntityAttribute>;

type ProgramTrackedEntityAttributeFormDefaults = Pick<
  NewProgramTrackedEntityAttribute,
  'id' | 'created' | 'updated' | 'displayInList' | 'mandatory' | 'allowFutureDate' | 'searchable'
>;

type ProgramTrackedEntityAttributeFormGroupContent = {
  id: FormControl<ProgramTrackedEntityAttributeFormRawValue['id'] | NewProgramTrackedEntityAttribute['id']>;
  uid: FormControl<ProgramTrackedEntityAttributeFormRawValue['uid']>;
  code: FormControl<ProgramTrackedEntityAttributeFormRawValue['code']>;
  created: FormControl<ProgramTrackedEntityAttributeFormRawValue['created']>;
  updated: FormControl<ProgramTrackedEntityAttributeFormRawValue['updated']>;
  displayInList: FormControl<ProgramTrackedEntityAttributeFormRawValue['displayInList']>;
  sortOrder: FormControl<ProgramTrackedEntityAttributeFormRawValue['sortOrder']>;
  mandatory: FormControl<ProgramTrackedEntityAttributeFormRawValue['mandatory']>;
  allowFutureDate: FormControl<ProgramTrackedEntityAttributeFormRawValue['allowFutureDate']>;
  searchable: FormControl<ProgramTrackedEntityAttributeFormRawValue['searchable']>;
  defaultValue: FormControl<ProgramTrackedEntityAttributeFormRawValue['defaultValue']>;
  program: FormControl<ProgramTrackedEntityAttributeFormRawValue['program']>;
  attribute: FormControl<ProgramTrackedEntityAttributeFormRawValue['attribute']>;
  createdBy: FormControl<ProgramTrackedEntityAttributeFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramTrackedEntityAttributeFormRawValue['updatedBy']>;
};

export type ProgramTrackedEntityAttributeFormGroup = FormGroup<ProgramTrackedEntityAttributeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramTrackedEntityAttributeFormService {
  createProgramTrackedEntityAttributeFormGroup(
    programTrackedEntityAttribute: ProgramTrackedEntityAttributeFormGroupInput = { id: null }
  ): ProgramTrackedEntityAttributeFormGroup {
    const programTrackedEntityAttributeRawValue = this.convertProgramTrackedEntityAttributeToProgramTrackedEntityAttributeRawValue({
      ...this.getFormDefaults(),
      ...programTrackedEntityAttribute,
    });
    return new FormGroup<ProgramTrackedEntityAttributeFormGroupContent>({
      id: new FormControl(
        { value: programTrackedEntityAttributeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programTrackedEntityAttributeRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(programTrackedEntityAttributeRawValue.code),
      created: new FormControl(programTrackedEntityAttributeRawValue.created),
      updated: new FormControl(programTrackedEntityAttributeRawValue.updated),
      displayInList: new FormControl(programTrackedEntityAttributeRawValue.displayInList),
      sortOrder: new FormControl(programTrackedEntityAttributeRawValue.sortOrder),
      mandatory: new FormControl(programTrackedEntityAttributeRawValue.mandatory),
      allowFutureDate: new FormControl(programTrackedEntityAttributeRawValue.allowFutureDate),
      searchable: new FormControl(programTrackedEntityAttributeRawValue.searchable),
      defaultValue: new FormControl(programTrackedEntityAttributeRawValue.defaultValue),
      program: new FormControl(programTrackedEntityAttributeRawValue.program, {
        validators: [Validators.required],
      }),
      attribute: new FormControl(programTrackedEntityAttributeRawValue.attribute, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(programTrackedEntityAttributeRawValue.createdBy),
      updatedBy: new FormControl(programTrackedEntityAttributeRawValue.updatedBy),
    });
  }

  getProgramTrackedEntityAttribute(
    form: ProgramTrackedEntityAttributeFormGroup
  ): IProgramTrackedEntityAttribute | NewProgramTrackedEntityAttribute {
    return this.convertProgramTrackedEntityAttributeRawValueToProgramTrackedEntityAttribute(
      form.getRawValue() as ProgramTrackedEntityAttributeFormRawValue | NewProgramTrackedEntityAttributeFormRawValue
    );
  }

  resetForm(
    form: ProgramTrackedEntityAttributeFormGroup,
    programTrackedEntityAttribute: ProgramTrackedEntityAttributeFormGroupInput
  ): void {
    const programTrackedEntityAttributeRawValue = this.convertProgramTrackedEntityAttributeToProgramTrackedEntityAttributeRawValue({
      ...this.getFormDefaults(),
      ...programTrackedEntityAttribute,
    });
    form.reset(
      {
        ...programTrackedEntityAttributeRawValue,
        id: { value: programTrackedEntityAttributeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramTrackedEntityAttributeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      displayInList: false,
      mandatory: false,
      allowFutureDate: false,
      searchable: false,
    };
  }

  private convertProgramTrackedEntityAttributeRawValueToProgramTrackedEntityAttribute(
    rawProgramTrackedEntityAttribute: ProgramTrackedEntityAttributeFormRawValue | NewProgramTrackedEntityAttributeFormRawValue
  ): IProgramTrackedEntityAttribute | NewProgramTrackedEntityAttribute {
    return {
      ...rawProgramTrackedEntityAttribute,
      created: dayjs(rawProgramTrackedEntityAttribute.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgramTrackedEntityAttribute.updated, DATE_TIME_FORMAT),
    };
  }

  private convertProgramTrackedEntityAttributeToProgramTrackedEntityAttributeRawValue(
    programTrackedEntityAttribute:
      | IProgramTrackedEntityAttribute
      | (Partial<NewProgramTrackedEntityAttribute> & ProgramTrackedEntityAttributeFormDefaults)
  ): ProgramTrackedEntityAttributeFormRawValue | PartialWithRequiredKeyOf<NewProgramTrackedEntityAttributeFormRawValue> {
    return {
      ...programTrackedEntityAttribute,
      created: programTrackedEntityAttribute.created ? programTrackedEntityAttribute.created.format(DATE_TIME_FORMAT) : undefined,
      updated: programTrackedEntityAttribute.updated ? programTrackedEntityAttribute.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
