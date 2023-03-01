import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityDataValueAudit, NewTrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityDataValueAudit for edit and NewTrackedEntityDataValueAuditFormGroupInput for create.
 */
type TrackedEntityDataValueAuditFormGroupInput = ITrackedEntityDataValueAudit | PartialWithRequiredKeyOf<NewTrackedEntityDataValueAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityDataValueAudit | NewTrackedEntityDataValueAudit> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityDataValueAuditFormRawValue = FormValueOf<ITrackedEntityDataValueAudit>;

type NewTrackedEntityDataValueAuditFormRawValue = FormValueOf<NewTrackedEntityDataValueAudit>;

type TrackedEntityDataValueAuditFormDefaults = Pick<NewTrackedEntityDataValueAudit, 'id' | 'created' | 'updated' | 'providedElsewhere'>;

type TrackedEntityDataValueAuditFormGroupContent = {
  id: FormControl<TrackedEntityDataValueAuditFormRawValue['id'] | NewTrackedEntityDataValueAudit['id']>;
  value: FormControl<TrackedEntityDataValueAuditFormRawValue['value']>;
  created: FormControl<TrackedEntityDataValueAuditFormRawValue['created']>;
  updated: FormControl<TrackedEntityDataValueAuditFormRawValue['updated']>;
  modifiedBy: FormControl<TrackedEntityDataValueAuditFormRawValue['modifiedBy']>;
  providedElsewhere: FormControl<TrackedEntityDataValueAuditFormRawValue['providedElsewhere']>;
  auditType: FormControl<TrackedEntityDataValueAuditFormRawValue['auditType']>;
  programStageInstance: FormControl<TrackedEntityDataValueAuditFormRawValue['programStageInstance']>;
  dataElement: FormControl<TrackedEntityDataValueAuditFormRawValue['dataElement']>;
  period: FormControl<TrackedEntityDataValueAuditFormRawValue['period']>;
};

export type TrackedEntityDataValueAuditFormGroup = FormGroup<TrackedEntityDataValueAuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityDataValueAuditFormService {
  createTrackedEntityDataValueAuditFormGroup(
    trackedEntityDataValueAudit: TrackedEntityDataValueAuditFormGroupInput = { id: null }
  ): TrackedEntityDataValueAuditFormGroup {
    const trackedEntityDataValueAuditRawValue = this.convertTrackedEntityDataValueAuditToTrackedEntityDataValueAuditRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityDataValueAudit,
    });
    return new FormGroup<TrackedEntityDataValueAuditFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityDataValueAuditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      value: new FormControl(trackedEntityDataValueAuditRawValue.value, {
        validators: [Validators.maxLength(50000)],
      }),
      created: new FormControl(trackedEntityDataValueAuditRawValue.created),
      updated: new FormControl(trackedEntityDataValueAuditRawValue.updated),
      modifiedBy: new FormControl(trackedEntityDataValueAuditRawValue.modifiedBy),
      providedElsewhere: new FormControl(trackedEntityDataValueAuditRawValue.providedElsewhere),
      auditType: new FormControl(trackedEntityDataValueAuditRawValue.auditType),
      programStageInstance: new FormControl(trackedEntityDataValueAuditRawValue.programStageInstance, {
        validators: [Validators.required],
      }),
      dataElement: new FormControl(trackedEntityDataValueAuditRawValue.dataElement, {
        validators: [Validators.required],
      }),
      period: new FormControl(trackedEntityDataValueAuditRawValue.period),
    });
  }

  getTrackedEntityDataValueAudit(
    form: TrackedEntityDataValueAuditFormGroup
  ): ITrackedEntityDataValueAudit | NewTrackedEntityDataValueAudit {
    return this.convertTrackedEntityDataValueAuditRawValueToTrackedEntityDataValueAudit(
      form.getRawValue() as TrackedEntityDataValueAuditFormRawValue | NewTrackedEntityDataValueAuditFormRawValue
    );
  }

  resetForm(form: TrackedEntityDataValueAuditFormGroup, trackedEntityDataValueAudit: TrackedEntityDataValueAuditFormGroupInput): void {
    const trackedEntityDataValueAuditRawValue = this.convertTrackedEntityDataValueAuditToTrackedEntityDataValueAuditRawValue({
      ...this.getFormDefaults(),
      ...trackedEntityDataValueAudit,
    });
    form.reset(
      {
        ...trackedEntityDataValueAuditRawValue,
        id: { value: trackedEntityDataValueAuditRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityDataValueAuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      providedElsewhere: false,
    };
  }

  private convertTrackedEntityDataValueAuditRawValueToTrackedEntityDataValueAudit(
    rawTrackedEntityDataValueAudit: TrackedEntityDataValueAuditFormRawValue | NewTrackedEntityDataValueAuditFormRawValue
  ): ITrackedEntityDataValueAudit | NewTrackedEntityDataValueAudit {
    return {
      ...rawTrackedEntityDataValueAudit,
      created: dayjs(rawTrackedEntityDataValueAudit.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityDataValueAudit.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityDataValueAuditToTrackedEntityDataValueAuditRawValue(
    trackedEntityDataValueAudit:
      | ITrackedEntityDataValueAudit
      | (Partial<NewTrackedEntityDataValueAudit> & TrackedEntityDataValueAuditFormDefaults)
  ): TrackedEntityDataValueAuditFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityDataValueAuditFormRawValue> {
    return {
      ...trackedEntityDataValueAudit,
      created: trackedEntityDataValueAudit.created ? trackedEntityDataValueAudit.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityDataValueAudit.updated ? trackedEntityDataValueAudit.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
