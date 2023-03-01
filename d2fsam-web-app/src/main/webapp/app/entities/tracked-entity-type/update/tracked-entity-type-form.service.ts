import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityType, NewTrackedEntityType } from '../tracked-entity-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityType for edit and NewTrackedEntityTypeFormGroupInput for create.
 */
type TrackedEntityTypeFormGroupInput = ITrackedEntityType | PartialWithRequiredKeyOf<NewTrackedEntityType>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityType | NewTrackedEntityType> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityTypeFormRawValue = FormValueOf<ITrackedEntityType>;

type NewTrackedEntityTypeFormRawValue = FormValueOf<NewTrackedEntityType>;

type TrackedEntityTypeFormDefaults = Pick<NewTrackedEntityType, 'id' | 'created' | 'updated' | 'allowAuditLog'>;

type TrackedEntityTypeFormGroupContent = {
  id: FormControl<TrackedEntityTypeFormRawValue['id'] | NewTrackedEntityType['id']>;
  uid: FormControl<TrackedEntityTypeFormRawValue['uid']>;
  code: FormControl<TrackedEntityTypeFormRawValue['code']>;
  created: FormControl<TrackedEntityTypeFormRawValue['created']>;
  updated: FormControl<TrackedEntityTypeFormRawValue['updated']>;
  name: FormControl<TrackedEntityTypeFormRawValue['name']>;
  description: FormControl<TrackedEntityTypeFormRawValue['description']>;
  maxTeiCountToReturn: FormControl<TrackedEntityTypeFormRawValue['maxTeiCountToReturn']>;
  allowAuditLog: FormControl<TrackedEntityTypeFormRawValue['allowAuditLog']>;
  featureType: FormControl<TrackedEntityTypeFormRawValue['featureType']>;
  createdBy: FormControl<TrackedEntityTypeFormRawValue['createdBy']>;
  updatedBy: FormControl<TrackedEntityTypeFormRawValue['updatedBy']>;
};

export type TrackedEntityTypeFormGroup = FormGroup<TrackedEntityTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityTypeFormService {
  createTrackedEntityTypeFormGroup(trackedEntityType: TrackedEntityTypeFormGroupInput = { id: null }): TrackedEntityTypeFormGroup {
    const trackedEntityTypeRawValue = this.convertTrackedEntityTypeToTrackedEntityTypeRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityType,
    });
    return new FormGroup<TrackedEntityTypeFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(trackedEntityTypeRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(trackedEntityTypeRawValue.code),
      created: new FormControl(trackedEntityTypeRawValue.created),
      updated: new FormControl(trackedEntityTypeRawValue.updated),
      name: new FormControl(trackedEntityTypeRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(trackedEntityTypeRawValue.description),
      maxTeiCountToReturn: new FormControl(trackedEntityTypeRawValue.maxTeiCountToReturn),
      allowAuditLog: new FormControl(trackedEntityTypeRawValue.allowAuditLog),
      featureType: new FormControl(trackedEntityTypeRawValue.featureType),
      createdBy: new FormControl(trackedEntityTypeRawValue.createdBy),
      updatedBy: new FormControl(trackedEntityTypeRawValue.updatedBy),
    });
  }

  getTrackedEntityType(form: TrackedEntityTypeFormGroup): ITrackedEntityType | NewTrackedEntityType {
    return this.convertTrackedEntityTypeRawValueToTrackedEntityType(
      form.getRawValue() as TrackedEntityTypeFormRawValue | NewTrackedEntityTypeFormRawValue
    );
  }

  resetForm(form: TrackedEntityTypeFormGroup, trackedEntityType: TrackedEntityTypeFormGroupInput): void {
    const trackedEntityTypeRawValue = this.convertTrackedEntityTypeToTrackedEntityTypeRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityType,
    });
    form.reset(
      {
        ...trackedEntityTypeRawValue,
        id: { value: trackedEntityTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityTypeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      allowAuditLog: false,
    };
  }

  private convertTrackedEntityTypeRawValueToTrackedEntityType(
    rawTrackedEntityType: TrackedEntityTypeFormRawValue | NewTrackedEntityTypeFormRawValue
  ): ITrackedEntityType | NewTrackedEntityType {
    return {
      ...rawTrackedEntityType,
      created: dayjs(rawTrackedEntityType.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityType.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityTypeToTrackedEntityTypeRawValue(
    trackedEntityType: ITrackedEntityType | (Partial<NewTrackedEntityType> & TrackedEntityTypeFormDefaults)
  ): TrackedEntityTypeFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityTypeFormRawValue> {
    return {
      ...trackedEntityType,
      created: trackedEntityType.created ? trackedEntityType.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityType.updated ? trackedEntityType.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
