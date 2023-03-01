import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityTypeAttribute, NewTrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityTypeAttribute for edit and NewTrackedEntityTypeAttributeFormGroupInput for create.
 */
type TrackedEntityTypeAttributeFormGroupInput = ITrackedEntityTypeAttribute | PartialWithRequiredKeyOf<NewTrackedEntityTypeAttribute>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityTypeAttribute | NewTrackedEntityTypeAttribute> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityTypeAttributeFormRawValue = FormValueOf<ITrackedEntityTypeAttribute>;

type NewTrackedEntityTypeAttributeFormRawValue = FormValueOf<NewTrackedEntityTypeAttribute>;

type TrackedEntityTypeAttributeFormDefaults = Pick<
  NewTrackedEntityTypeAttribute,
  'id' | 'created' | 'updated' | 'displayInList' | 'mandatory' | 'searchable'
>;

type TrackedEntityTypeAttributeFormGroupContent = {
  id: FormControl<TrackedEntityTypeAttributeFormRawValue['id'] | NewTrackedEntityTypeAttribute['id']>;
  uid: FormControl<TrackedEntityTypeAttributeFormRawValue['uid']>;
  code: FormControl<TrackedEntityTypeAttributeFormRawValue['code']>;
  created: FormControl<TrackedEntityTypeAttributeFormRawValue['created']>;
  updated: FormControl<TrackedEntityTypeAttributeFormRawValue['updated']>;
  displayInList: FormControl<TrackedEntityTypeAttributeFormRawValue['displayInList']>;
  mandatory: FormControl<TrackedEntityTypeAttributeFormRawValue['mandatory']>;
  searchable: FormControl<TrackedEntityTypeAttributeFormRawValue['searchable']>;
  trackedEntityAttribute: FormControl<TrackedEntityTypeAttributeFormRawValue['trackedEntityAttribute']>;
  trackedEntityType: FormControl<TrackedEntityTypeAttributeFormRawValue['trackedEntityType']>;
  updatedBy: FormControl<TrackedEntityTypeAttributeFormRawValue['updatedBy']>;
};

export type TrackedEntityTypeAttributeFormGroup = FormGroup<TrackedEntityTypeAttributeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityTypeAttributeFormService {
  createTrackedEntityTypeAttributeFormGroup(
    trackedEntityTypeAttribute: TrackedEntityTypeAttributeFormGroupInput = { id: null }
  ): TrackedEntityTypeAttributeFormGroup {
    const trackedEntityTypeAttributeRawValue = this.convertTrackedEntityTypeAttributeToTrackedEntityTypeAttributeRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityTypeAttribute,
    });
    return new FormGroup<TrackedEntityTypeAttributeFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityTypeAttributeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(trackedEntityTypeAttributeRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(trackedEntityTypeAttributeRawValue.code),
      created: new FormControl(trackedEntityTypeAttributeRawValue.created),
      updated: new FormControl(trackedEntityTypeAttributeRawValue.updated),
      displayInList: new FormControl(trackedEntityTypeAttributeRawValue.displayInList),
      mandatory: new FormControl(trackedEntityTypeAttributeRawValue.mandatory),
      searchable: new FormControl(trackedEntityTypeAttributeRawValue.searchable),
      trackedEntityAttribute: new FormControl(trackedEntityTypeAttributeRawValue.trackedEntityAttribute, {
        validators: [Validators.required],
      }),
      trackedEntityType: new FormControl(trackedEntityTypeAttributeRawValue.trackedEntityType),
      updatedBy: new FormControl(trackedEntityTypeAttributeRawValue.updatedBy),
    });
  }

  getTrackedEntityTypeAttribute(form: TrackedEntityTypeAttributeFormGroup): ITrackedEntityTypeAttribute | NewTrackedEntityTypeAttribute {
    return this.convertTrackedEntityTypeAttributeRawValueToTrackedEntityTypeAttribute(
      form.getRawValue() as TrackedEntityTypeAttributeFormRawValue | NewTrackedEntityTypeAttributeFormRawValue
    );
  }

  resetForm(form: TrackedEntityTypeAttributeFormGroup, trackedEntityTypeAttribute: TrackedEntityTypeAttributeFormGroupInput): void {
    const trackedEntityTypeAttributeRawValue = this.convertTrackedEntityTypeAttributeToTrackedEntityTypeAttributeRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityTypeAttribute,
    });
    form.reset(
      {
        ...trackedEntityTypeAttributeRawValue,
        id: { value: trackedEntityTypeAttributeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityTypeAttributeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      displayInList: false,
      mandatory: false,
      searchable: false,
    };
  }

  private convertTrackedEntityTypeAttributeRawValueToTrackedEntityTypeAttribute(
    rawTrackedEntityTypeAttribute: TrackedEntityTypeAttributeFormRawValue | NewTrackedEntityTypeAttributeFormRawValue
  ): ITrackedEntityTypeAttribute | NewTrackedEntityTypeAttribute {
    return {
      ...rawTrackedEntityTypeAttribute,
      created: dayjs(rawTrackedEntityTypeAttribute.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityTypeAttribute.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityTypeAttributeToTrackedEntityTypeAttributeRawValue(
    trackedEntityTypeAttribute:
      | ITrackedEntityTypeAttribute
      | (Partial<NewTrackedEntityTypeAttribute> & TrackedEntityTypeAttributeFormDefaults)
  ): TrackedEntityTypeAttributeFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityTypeAttributeFormRawValue> {
    return {
      ...trackedEntityTypeAttribute,
      created: trackedEntityTypeAttribute.created ? trackedEntityTypeAttribute.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityTypeAttribute.updated ? trackedEntityTypeAttribute.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
