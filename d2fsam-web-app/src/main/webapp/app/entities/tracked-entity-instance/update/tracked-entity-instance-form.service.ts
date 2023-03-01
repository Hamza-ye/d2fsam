import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityInstance, NewTrackedEntityInstance } from '../tracked-entity-instance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityInstance for edit and NewTrackedEntityInstanceFormGroupInput for create.
 */
type TrackedEntityInstanceFormGroupInput = ITrackedEntityInstance | PartialWithRequiredKeyOf<NewTrackedEntityInstance>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityInstance | NewTrackedEntityInstance> = Omit<
  T,
  'created' | 'updated' | 'createdAtClient' | 'updatedAtClient' | 'lastSynchronized'
> & {
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  lastSynchronized?: string | null;
};

type TrackedEntityInstanceFormRawValue = FormValueOf<ITrackedEntityInstance>;

type NewTrackedEntityInstanceFormRawValue = FormValueOf<NewTrackedEntityInstance>;

type TrackedEntityInstanceFormDefaults = Pick<
  NewTrackedEntityInstance,
  'id' | 'created' | 'updated' | 'createdAtClient' | 'updatedAtClient' | 'lastSynchronized' | 'potentialDuplicate' | 'deleted' | 'inactive'
>;

type TrackedEntityInstanceFormGroupContent = {
  id: FormControl<TrackedEntityInstanceFormRawValue['id'] | NewTrackedEntityInstance['id']>;
  uid: FormControl<TrackedEntityInstanceFormRawValue['uid']>;
  uuid: FormControl<TrackedEntityInstanceFormRawValue['uuid']>;
  code: FormControl<TrackedEntityInstanceFormRawValue['code']>;
  created: FormControl<TrackedEntityInstanceFormRawValue['created']>;
  updated: FormControl<TrackedEntityInstanceFormRawValue['updated']>;
  createdAtClient: FormControl<TrackedEntityInstanceFormRawValue['createdAtClient']>;
  updatedAtClient: FormControl<TrackedEntityInstanceFormRawValue['updatedAtClient']>;
  lastSynchronized: FormControl<TrackedEntityInstanceFormRawValue['lastSynchronized']>;
  featureType: FormControl<TrackedEntityInstanceFormRawValue['featureType']>;
  coordinates: FormControl<TrackedEntityInstanceFormRawValue['coordinates']>;
  potentialDuplicate: FormControl<TrackedEntityInstanceFormRawValue['potentialDuplicate']>;
  deleted: FormControl<TrackedEntityInstanceFormRawValue['deleted']>;
  storedBy: FormControl<TrackedEntityInstanceFormRawValue['storedBy']>;
  inactive: FormControl<TrackedEntityInstanceFormRawValue['inactive']>;
  period: FormControl<TrackedEntityInstanceFormRawValue['period']>;
  organisationUnit: FormControl<TrackedEntityInstanceFormRawValue['organisationUnit']>;
  trackedEntityType: FormControl<TrackedEntityInstanceFormRawValue['trackedEntityType']>;
  createdBy: FormControl<TrackedEntityInstanceFormRawValue['createdBy']>;
  updatedBy: FormControl<TrackedEntityInstanceFormRawValue['updatedBy']>;
};

export type TrackedEntityInstanceFormGroup = FormGroup<TrackedEntityInstanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceFormService {
  createTrackedEntityInstanceFormGroup(
    trackedEntityInstance: TrackedEntityInstanceFormGroupInput = { id: null }
  ): TrackedEntityInstanceFormGroup {
    const trackedEntityInstanceRawValue = this.convertTrackedEntityInstanceToTrackedEntityInstanceRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityInstance,
    });
    return new FormGroup<TrackedEntityInstanceFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityInstanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(trackedEntityInstanceRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      uuid: new FormControl(trackedEntityInstanceRawValue.uuid),
      code: new FormControl(trackedEntityInstanceRawValue.code),
      created: new FormControl(trackedEntityInstanceRawValue.created),
      updated: new FormControl(trackedEntityInstanceRawValue.updated),
      createdAtClient: new FormControl(trackedEntityInstanceRawValue.createdAtClient),
      updatedAtClient: new FormControl(trackedEntityInstanceRawValue.updatedAtClient),
      lastSynchronized: new FormControl(trackedEntityInstanceRawValue.lastSynchronized),
      featureType: new FormControl(trackedEntityInstanceRawValue.featureType),
      coordinates: new FormControl(trackedEntityInstanceRawValue.coordinates),
      potentialDuplicate: new FormControl(trackedEntityInstanceRawValue.potentialDuplicate),
      deleted: new FormControl(trackedEntityInstanceRawValue.deleted),
      storedBy: new FormControl(trackedEntityInstanceRawValue.storedBy),
      inactive: new FormControl(trackedEntityInstanceRawValue.inactive),
      period: new FormControl(trackedEntityInstanceRawValue.period),
      organisationUnit: new FormControl(trackedEntityInstanceRawValue.organisationUnit, {
        validators: [Validators.required],
      }),
      trackedEntityType: new FormControl(trackedEntityInstanceRawValue.trackedEntityType, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(trackedEntityInstanceRawValue.createdBy),
      updatedBy: new FormControl(trackedEntityInstanceRawValue.updatedBy),
    });
  }

  getTrackedEntityInstance(form: TrackedEntityInstanceFormGroup): ITrackedEntityInstance | NewTrackedEntityInstance {
    return this.convertTrackedEntityInstanceRawValueToTrackedEntityInstance(
      form.getRawValue() as TrackedEntityInstanceFormRawValue | NewTrackedEntityInstanceFormRawValue
    );
  }

  resetForm(form: TrackedEntityInstanceFormGroup, trackedEntityInstance: TrackedEntityInstanceFormGroupInput): void {
    const trackedEntityInstanceRawValue = this.convertTrackedEntityInstanceToTrackedEntityInstanceRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityInstance,
    });
    form.reset(
      {
        ...trackedEntityInstanceRawValue,
        id: { value: trackedEntityInstanceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityInstanceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      createdAtClient: currentTime,
      updatedAtClient: currentTime,
      lastSynchronized: currentTime,
      potentialDuplicate: false,
      deleted: false,
      inactive: false,
    };
  }

  private convertTrackedEntityInstanceRawValueToTrackedEntityInstance(
    rawTrackedEntityInstance: TrackedEntityInstanceFormRawValue | NewTrackedEntityInstanceFormRawValue
  ): ITrackedEntityInstance | NewTrackedEntityInstance {
    return {
      ...rawTrackedEntityInstance,
      created: dayjs(rawTrackedEntityInstance.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityInstance.updated, DATE_TIME_FORMAT),
      createdAtClient: dayjs(rawTrackedEntityInstance.createdAtClient, DATE_TIME_FORMAT),
      updatedAtClient: dayjs(rawTrackedEntityInstance.updatedAtClient, DATE_TIME_FORMAT),
      lastSynchronized: dayjs(rawTrackedEntityInstance.lastSynchronized, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityInstanceToTrackedEntityInstanceRawValue(
    trackedEntityInstance: ITrackedEntityInstance | (Partial<NewTrackedEntityInstance> & TrackedEntityInstanceFormDefaults)
  ): TrackedEntityInstanceFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityInstanceFormRawValue> {
    return {
      ...trackedEntityInstance,
      created: trackedEntityInstance.created ? trackedEntityInstance.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityInstance.updated ? trackedEntityInstance.updated.format(DATE_TIME_FORMAT) : undefined,
      createdAtClient: trackedEntityInstance.createdAtClient ? trackedEntityInstance.createdAtClient.format(DATE_TIME_FORMAT) : undefined,
      updatedAtClient: trackedEntityInstance.updatedAtClient ? trackedEntityInstance.updatedAtClient.format(DATE_TIME_FORMAT) : undefined,
      lastSynchronized: trackedEntityInstance.lastSynchronized
        ? trackedEntityInstance.lastSynchronized.format(DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
