import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityInstanceAudit, NewTrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityInstanceAudit for edit and NewTrackedEntityInstanceAuditFormGroupInput for create.
 */
type TrackedEntityInstanceAuditFormGroupInput = ITrackedEntityInstanceAudit | PartialWithRequiredKeyOf<NewTrackedEntityInstanceAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityInstanceAudit | NewTrackedEntityInstanceAudit> = Omit<T, 'created'> & {
  created?: string | null;
};

type TrackedEntityInstanceAuditFormRawValue = FormValueOf<ITrackedEntityInstanceAudit>;

type NewTrackedEntityInstanceAuditFormRawValue = FormValueOf<NewTrackedEntityInstanceAudit>;

type TrackedEntityInstanceAuditFormDefaults = Pick<NewTrackedEntityInstanceAudit, 'id' | 'created'>;

type TrackedEntityInstanceAuditFormGroupContent = {
  id: FormControl<TrackedEntityInstanceAuditFormRawValue['id'] | NewTrackedEntityInstanceAudit['id']>;
  trackedEntityInstance: FormControl<TrackedEntityInstanceAuditFormRawValue['trackedEntityInstance']>;
  comment: FormControl<TrackedEntityInstanceAuditFormRawValue['comment']>;
  created: FormControl<TrackedEntityInstanceAuditFormRawValue['created']>;
  accessedBy: FormControl<TrackedEntityInstanceAuditFormRawValue['accessedBy']>;
  auditType: FormControl<TrackedEntityInstanceAuditFormRawValue['auditType']>;
};

export type TrackedEntityInstanceAuditFormGroup = FormGroup<TrackedEntityInstanceAuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceAuditFormService {
  createTrackedEntityInstanceAuditFormGroup(
    trackedEntityInstanceAudit: TrackedEntityInstanceAuditFormGroupInput = { id: null }
  ): TrackedEntityInstanceAuditFormGroup {
    const trackedEntityInstanceAuditRawValue = this.convertTrackedEntityInstanceAuditToTrackedEntityInstanceAuditRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityInstanceAudit,
    });
    return new FormGroup<TrackedEntityInstanceAuditFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityInstanceAuditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      trackedEntityInstance: new FormControl(trackedEntityInstanceAuditRawValue.trackedEntityInstance),
      comment: new FormControl(trackedEntityInstanceAuditRawValue.comment),
      created: new FormControl(trackedEntityInstanceAuditRawValue.created),
      accessedBy: new FormControl(trackedEntityInstanceAuditRawValue.accessedBy),
      auditType: new FormControl(trackedEntityInstanceAuditRawValue.auditType),
    });
  }

  getTrackedEntityInstanceAudit(form: TrackedEntityInstanceAuditFormGroup): ITrackedEntityInstanceAudit | NewTrackedEntityInstanceAudit {
    return this.convertTrackedEntityInstanceAuditRawValueToTrackedEntityInstanceAudit(
      form.getRawValue() as TrackedEntityInstanceAuditFormRawValue | NewTrackedEntityInstanceAuditFormRawValue
    );
  }

  resetForm(form: TrackedEntityInstanceAuditFormGroup, trackedEntityInstanceAudit: TrackedEntityInstanceAuditFormGroupInput): void {
    const trackedEntityInstanceAuditRawValue = this.convertTrackedEntityInstanceAuditToTrackedEntityInstanceAuditRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityInstanceAudit,
    });
    form.reset(
      {
        ...trackedEntityInstanceAuditRawValue,
        id: { value: trackedEntityInstanceAuditRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityInstanceAuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
    };
  }

  private convertTrackedEntityInstanceAuditRawValueToTrackedEntityInstanceAudit(
    rawTrackedEntityInstanceAudit: TrackedEntityInstanceAuditFormRawValue | NewTrackedEntityInstanceAuditFormRawValue
  ): ITrackedEntityInstanceAudit | NewTrackedEntityInstanceAudit {
    return {
      ...rawTrackedEntityInstanceAudit,
      created: dayjs(rawTrackedEntityInstanceAudit.created, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityInstanceAuditToTrackedEntityInstanceAuditRawValue(
    trackedEntityInstanceAudit:
      | ITrackedEntityInstanceAudit
      | (Partial<NewTrackedEntityInstanceAudit> & TrackedEntityInstanceAuditFormDefaults)
  ): TrackedEntityInstanceAuditFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityInstanceAuditFormRawValue> {
    return {
      ...trackedEntityInstanceAudit,
      created: trackedEntityInstanceAudit.created ? trackedEntityInstanceAudit.created.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
