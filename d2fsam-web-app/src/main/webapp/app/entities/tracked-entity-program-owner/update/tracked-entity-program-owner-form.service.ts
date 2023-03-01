import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityProgramOwner, NewTrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityProgramOwner for edit and NewTrackedEntityProgramOwnerFormGroupInput for create.
 */
type TrackedEntityProgramOwnerFormGroupInput = ITrackedEntityProgramOwner | PartialWithRequiredKeyOf<NewTrackedEntityProgramOwner>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityProgramOwner | NewTrackedEntityProgramOwner> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityProgramOwnerFormRawValue = FormValueOf<ITrackedEntityProgramOwner>;

type NewTrackedEntityProgramOwnerFormRawValue = FormValueOf<NewTrackedEntityProgramOwner>;

type TrackedEntityProgramOwnerFormDefaults = Pick<NewTrackedEntityProgramOwner, 'id' | 'created' | 'updated'>;

type TrackedEntityProgramOwnerFormGroupContent = {
  id: FormControl<TrackedEntityProgramOwnerFormRawValue['id'] | NewTrackedEntityProgramOwner['id']>;
  created: FormControl<TrackedEntityProgramOwnerFormRawValue['created']>;
  updated: FormControl<TrackedEntityProgramOwnerFormRawValue['updated']>;
  createdBy: FormControl<TrackedEntityProgramOwnerFormRawValue['createdBy']>;
  entityInstance: FormControl<TrackedEntityProgramOwnerFormRawValue['entityInstance']>;
  program: FormControl<TrackedEntityProgramOwnerFormRawValue['program']>;
  organisationUnit: FormControl<TrackedEntityProgramOwnerFormRawValue['organisationUnit']>;
};

export type TrackedEntityProgramOwnerFormGroup = FormGroup<TrackedEntityProgramOwnerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityProgramOwnerFormService {
  createTrackedEntityProgramOwnerFormGroup(
    trackedEntityProgramOwner: TrackedEntityProgramOwnerFormGroupInput = { id: null }
  ): TrackedEntityProgramOwnerFormGroup {
    const trackedEntityProgramOwnerRawValue = this.convertTrackedEntityProgramOwnerToTrackedEntityProgramOwnerRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityProgramOwner,
    });
    return new FormGroup<TrackedEntityProgramOwnerFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityProgramOwnerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      created: new FormControl(trackedEntityProgramOwnerRawValue.created),
      updated: new FormControl(trackedEntityProgramOwnerRawValue.updated),
      createdBy: new FormControl(trackedEntityProgramOwnerRawValue.createdBy),
      entityInstance: new FormControl(trackedEntityProgramOwnerRawValue.entityInstance, {
        validators: [Validators.required],
      }),
      program: new FormControl(trackedEntityProgramOwnerRawValue.program, {
        validators: [Validators.required],
      }),
      organisationUnit: new FormControl(trackedEntityProgramOwnerRawValue.organisationUnit, {
        validators: [Validators.required],
      }),
    });
  }

  getTrackedEntityProgramOwner(form: TrackedEntityProgramOwnerFormGroup): ITrackedEntityProgramOwner | NewTrackedEntityProgramOwner {
    return this.convertTrackedEntityProgramOwnerRawValueToTrackedEntityProgramOwner(
      form.getRawValue() as TrackedEntityProgramOwnerFormRawValue | NewTrackedEntityProgramOwnerFormRawValue
    );
  }

  resetForm(form: TrackedEntityProgramOwnerFormGroup, trackedEntityProgramOwner: TrackedEntityProgramOwnerFormGroupInput): void {
    const trackedEntityProgramOwnerRawValue = this.convertTrackedEntityProgramOwnerToTrackedEntityProgramOwnerRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityProgramOwner,
    });
    form.reset(
      {
        ...trackedEntityProgramOwnerRawValue,
        id: { value: trackedEntityProgramOwnerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityProgramOwnerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertTrackedEntityProgramOwnerRawValueToTrackedEntityProgramOwner(
    rawTrackedEntityProgramOwner: TrackedEntityProgramOwnerFormRawValue | NewTrackedEntityProgramOwnerFormRawValue
  ): ITrackedEntityProgramOwner | NewTrackedEntityProgramOwner {
    return {
      ...rawTrackedEntityProgramOwner,
      created: dayjs(rawTrackedEntityProgramOwner.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityProgramOwner.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityProgramOwnerToTrackedEntityProgramOwnerRawValue(
    trackedEntityProgramOwner: ITrackedEntityProgramOwner | (Partial<NewTrackedEntityProgramOwner> & TrackedEntityProgramOwnerFormDefaults)
  ): TrackedEntityProgramOwnerFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityProgramOwnerFormRawValue> {
    return {
      ...trackedEntityProgramOwner,
      created: trackedEntityProgramOwner.created ? trackedEntityProgramOwner.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityProgramOwner.updated ? trackedEntityProgramOwner.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
