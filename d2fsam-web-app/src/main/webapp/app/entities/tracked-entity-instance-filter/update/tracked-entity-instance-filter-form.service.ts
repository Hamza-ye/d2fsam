import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityInstanceFilter, NewTrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityInstanceFilter for edit and NewTrackedEntityInstanceFilterFormGroupInput for create.
 */
type TrackedEntityInstanceFilterFormGroupInput = ITrackedEntityInstanceFilter | PartialWithRequiredKeyOf<NewTrackedEntityInstanceFilter>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityInstanceFilter | NewTrackedEntityInstanceFilter> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityInstanceFilterFormRawValue = FormValueOf<ITrackedEntityInstanceFilter>;

type NewTrackedEntityInstanceFilterFormRawValue = FormValueOf<NewTrackedEntityInstanceFilter>;

type TrackedEntityInstanceFilterFormDefaults = Pick<NewTrackedEntityInstanceFilter, 'id' | 'created' | 'updated'>;

type TrackedEntityInstanceFilterFormGroupContent = {
  id: FormControl<TrackedEntityInstanceFilterFormRawValue['id'] | NewTrackedEntityInstanceFilter['id']>;
  uid: FormControl<TrackedEntityInstanceFilterFormRawValue['uid']>;
  code: FormControl<TrackedEntityInstanceFilterFormRawValue['code']>;
  name: FormControl<TrackedEntityInstanceFilterFormRawValue['name']>;
  created: FormControl<TrackedEntityInstanceFilterFormRawValue['created']>;
  updated: FormControl<TrackedEntityInstanceFilterFormRawValue['updated']>;
  description: FormControl<TrackedEntityInstanceFilterFormRawValue['description']>;
  sortOrder: FormControl<TrackedEntityInstanceFilterFormRawValue['sortOrder']>;
  enrollmentStatus: FormControl<TrackedEntityInstanceFilterFormRawValue['enrollmentStatus']>;
  program: FormControl<TrackedEntityInstanceFilterFormRawValue['program']>;
  createdBy: FormControl<TrackedEntityInstanceFilterFormRawValue['createdBy']>;
  updatedBy: FormControl<TrackedEntityInstanceFilterFormRawValue['updatedBy']>;
};

export type TrackedEntityInstanceFilterFormGroup = FormGroup<TrackedEntityInstanceFilterFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceFilterFormService {
  createTrackedEntityInstanceFilterFormGroup(
    trackedEntityInstanceFilter: TrackedEntityInstanceFilterFormGroupInput = { id: null }
  ): TrackedEntityInstanceFilterFormGroup {
    const trackedEntityInstanceFilterRawValue = this.convertTrackedEntityInstanceFilterToTrackedEntityInstanceFilterRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityInstanceFilter,
    });
    return new FormGroup<TrackedEntityInstanceFilterFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityInstanceFilterRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(trackedEntityInstanceFilterRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(trackedEntityInstanceFilterRawValue.code),
      name: new FormControl(trackedEntityInstanceFilterRawValue.name),
      created: new FormControl(trackedEntityInstanceFilterRawValue.created),
      updated: new FormControl(trackedEntityInstanceFilterRawValue.updated),
      description: new FormControl(trackedEntityInstanceFilterRawValue.description),
      sortOrder: new FormControl(trackedEntityInstanceFilterRawValue.sortOrder),
      enrollmentStatus: new FormControl(trackedEntityInstanceFilterRawValue.enrollmentStatus),
      program: new FormControl(trackedEntityInstanceFilterRawValue.program),
      createdBy: new FormControl(trackedEntityInstanceFilterRawValue.createdBy),
      updatedBy: new FormControl(trackedEntityInstanceFilterRawValue.updatedBy),
    });
  }

  getTrackedEntityInstanceFilter(
    form: TrackedEntityInstanceFilterFormGroup
  ): ITrackedEntityInstanceFilter | NewTrackedEntityInstanceFilter {
    return this.convertTrackedEntityInstanceFilterRawValueToTrackedEntityInstanceFilter(
      form.getRawValue() as TrackedEntityInstanceFilterFormRawValue | NewTrackedEntityInstanceFilterFormRawValue
    );
  }

  resetForm(form: TrackedEntityInstanceFilterFormGroup, trackedEntityInstanceFilter: TrackedEntityInstanceFilterFormGroupInput): void {
    const trackedEntityInstanceFilterRawValue = this.convertTrackedEntityInstanceFilterToTrackedEntityInstanceFilterRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityInstanceFilter,
    });
    form.reset(
      {
        ...trackedEntityInstanceFilterRawValue,
        id: { value: trackedEntityInstanceFilterRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityInstanceFilterFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertTrackedEntityInstanceFilterRawValueToTrackedEntityInstanceFilter(
    rawTrackedEntityInstanceFilter: TrackedEntityInstanceFilterFormRawValue | NewTrackedEntityInstanceFilterFormRawValue
  ): ITrackedEntityInstanceFilter | NewTrackedEntityInstanceFilter {
    return {
      ...rawTrackedEntityInstanceFilter,
      created: dayjs(rawTrackedEntityInstanceFilter.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityInstanceFilter.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityInstanceFilterToTrackedEntityInstanceFilterRawValue(
    trackedEntityInstanceFilter:
      | ITrackedEntityInstanceFilter
      | (Partial<NewTrackedEntityInstanceFilter> & TrackedEntityInstanceFilterFormDefaults)
  ): TrackedEntityInstanceFilterFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityInstanceFilterFormRawValue> {
    return {
      ...trackedEntityInstanceFilter,
      created: trackedEntityInstanceFilter.created ? trackedEntityInstanceFilter.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityInstanceFilter.updated ? trackedEntityInstanceFilter.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
