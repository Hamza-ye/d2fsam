import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityAttributeValue, NewTrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityAttributeValue for edit and NewTrackedEntityAttributeValueFormGroupInput for create.
 */
type TrackedEntityAttributeValueFormGroupInput = ITrackedEntityAttributeValue | PartialWithRequiredKeyOf<NewTrackedEntityAttributeValue>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityAttributeValue | NewTrackedEntityAttributeValue> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityAttributeValueFormRawValue = FormValueOf<ITrackedEntityAttributeValue>;

type NewTrackedEntityAttributeValueFormRawValue = FormValueOf<NewTrackedEntityAttributeValue>;

type TrackedEntityAttributeValueFormDefaults = Pick<NewTrackedEntityAttributeValue, 'id' | 'created' | 'updated'>;

type TrackedEntityAttributeValueFormGroupContent = {
  id: FormControl<TrackedEntityAttributeValueFormRawValue['id'] | NewTrackedEntityAttributeValue['id']>;
  encryptedValue: FormControl<TrackedEntityAttributeValueFormRawValue['encryptedValue']>;
  plainValue: FormControl<TrackedEntityAttributeValueFormRawValue['plainValue']>;
  value: FormControl<TrackedEntityAttributeValueFormRawValue['value']>;
  created: FormControl<TrackedEntityAttributeValueFormRawValue['created']>;
  updated: FormControl<TrackedEntityAttributeValueFormRawValue['updated']>;
  storedBy: FormControl<TrackedEntityAttributeValueFormRawValue['storedBy']>;
  attribute: FormControl<TrackedEntityAttributeValueFormRawValue['attribute']>;
  entityInstance: FormControl<TrackedEntityAttributeValueFormRawValue['entityInstance']>;
};

export type TrackedEntityAttributeValueFormGroup = FormGroup<TrackedEntityAttributeValueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityAttributeValueFormService {
  createTrackedEntityAttributeValueFormGroup(
    trackedEntityAttributeValue: TrackedEntityAttributeValueFormGroupInput = { id: null }
  ): TrackedEntityAttributeValueFormGroup {
    const trackedEntityAttributeValueRawValue = this.convertTrackedEntityAttributeValueToTrackedEntityAttributeValueRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityAttributeValue,
    });
    return new FormGroup<TrackedEntityAttributeValueFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityAttributeValueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      encryptedValue: new FormControl(trackedEntityAttributeValueRawValue.encryptedValue),
      plainValue: new FormControl(trackedEntityAttributeValueRawValue.plainValue),
      value: new FormControl(trackedEntityAttributeValueRawValue.value),
      created: new FormControl(trackedEntityAttributeValueRawValue.created),
      updated: new FormControl(trackedEntityAttributeValueRawValue.updated),
      storedBy: new FormControl(trackedEntityAttributeValueRawValue.storedBy),
      attribute: new FormControl(trackedEntityAttributeValueRawValue.attribute, {
        validators: [Validators.required],
      }),
      entityInstance: new FormControl(trackedEntityAttributeValueRawValue.entityInstance, {
        validators: [Validators.required],
      }),
    });
  }

  getTrackedEntityAttributeValue(
    form: TrackedEntityAttributeValueFormGroup
  ): ITrackedEntityAttributeValue | NewTrackedEntityAttributeValue {
    return this.convertTrackedEntityAttributeValueRawValueToTrackedEntityAttributeValue(
      form.getRawValue() as TrackedEntityAttributeValueFormRawValue | NewTrackedEntityAttributeValueFormRawValue
    );
  }

  resetForm(form: TrackedEntityAttributeValueFormGroup, trackedEntityAttributeValue: TrackedEntityAttributeValueFormGroupInput): void {
    const trackedEntityAttributeValueRawValue = this.convertTrackedEntityAttributeValueToTrackedEntityAttributeValueRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityAttributeValue,
    });
    form.reset(
      {
        ...trackedEntityAttributeValueRawValue,
        id: { value: trackedEntityAttributeValueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityAttributeValueFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertTrackedEntityAttributeValueRawValueToTrackedEntityAttributeValue(
    rawTrackedEntityAttributeValue: TrackedEntityAttributeValueFormRawValue | NewTrackedEntityAttributeValueFormRawValue
  ): ITrackedEntityAttributeValue | NewTrackedEntityAttributeValue {
    return {
      ...rawTrackedEntityAttributeValue,
      created: dayjs(rawTrackedEntityAttributeValue.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityAttributeValue.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityAttributeValueToTrackedEntityAttributeValueRawValue(
    trackedEntityAttributeValue:
      | ITrackedEntityAttributeValue
      | (Partial<NewTrackedEntityAttributeValue> & TrackedEntityAttributeValueFormDefaults)
  ): TrackedEntityAttributeValueFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityAttributeValueFormRawValue> {
    return {
      ...trackedEntityAttributeValue,
      created: trackedEntityAttributeValue.created ? trackedEntityAttributeValue.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityAttributeValue.updated ? trackedEntityAttributeValue.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
