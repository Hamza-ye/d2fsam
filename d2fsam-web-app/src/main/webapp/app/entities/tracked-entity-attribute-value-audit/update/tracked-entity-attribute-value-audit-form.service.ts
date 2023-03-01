import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITrackedEntityAttributeValueAudit, NewTrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackedEntityAttributeValueAudit for edit and NewTrackedEntityAttributeValueAuditFormGroupInput for create.
 */
type TrackedEntityAttributeValueAuditFormGroupInput =
  | ITrackedEntityAttributeValueAudit
  | PartialWithRequiredKeyOf<NewTrackedEntityAttributeValueAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITrackedEntityAttributeValueAudit | NewTrackedEntityAttributeValueAudit> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type TrackedEntityAttributeValueAuditFormRawValue = FormValueOf<ITrackedEntityAttributeValueAudit>;

type NewTrackedEntityAttributeValueAuditFormRawValue = FormValueOf<NewTrackedEntityAttributeValueAudit>;

type TrackedEntityAttributeValueAuditFormDefaults = Pick<NewTrackedEntityAttributeValueAudit, 'id' | 'created' | 'updated'>;

type TrackedEntityAttributeValueAuditFormGroupContent = {
  id: FormControl<TrackedEntityAttributeValueAuditFormRawValue['id'] | NewTrackedEntityAttributeValueAudit['id']>;
  encryptedValue: FormControl<TrackedEntityAttributeValueAuditFormRawValue['encryptedValue']>;
  plainValue: FormControl<TrackedEntityAttributeValueAuditFormRawValue['plainValue']>;
  value: FormControl<TrackedEntityAttributeValueAuditFormRawValue['value']>;
  modifiedBy: FormControl<TrackedEntityAttributeValueAuditFormRawValue['modifiedBy']>;
  created: FormControl<TrackedEntityAttributeValueAuditFormRawValue['created']>;
  updated: FormControl<TrackedEntityAttributeValueAuditFormRawValue['updated']>;
  auditType: FormControl<TrackedEntityAttributeValueAuditFormRawValue['auditType']>;
  attribute: FormControl<TrackedEntityAttributeValueAuditFormRawValue['attribute']>;
  entityInstance: FormControl<TrackedEntityAttributeValueAuditFormRawValue['entityInstance']>;
};

export type TrackedEntityAttributeValueAuditFormGroup = FormGroup<TrackedEntityAttributeValueAuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityAttributeValueAuditFormService {
  createTrackedEntityAttributeValueAuditFormGroup(
    trackedEntityAttributeValueAudit: TrackedEntityAttributeValueAuditFormGroupInput = { id: null }
  ): TrackedEntityAttributeValueAuditFormGroup {
    const trackedEntityAttributeValueAuditRawValue = this.convertTrackedEntityAttributeValueAuditToTrackedEntityAttributeValueAuditRawValue(
      {
        ...this.getFormDefaults(),
        ...trackedEntityAttributeValueAudit,
      }
    );
    return new FormGroup<TrackedEntityAttributeValueAuditFormGroupContent>({
      id: new FormControl(
        { value: trackedEntityAttributeValueAuditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      encryptedValue: new FormControl(trackedEntityAttributeValueAuditRawValue.encryptedValue, {
        validators: [Validators.maxLength(50000)],
      }),
      plainValue: new FormControl(trackedEntityAttributeValueAuditRawValue.plainValue, {
        validators: [Validators.maxLength(1200)],
      }),
      value: new FormControl(trackedEntityAttributeValueAuditRawValue.value, {
        validators: [Validators.maxLength(1200)],
      }),
      modifiedBy: new FormControl(trackedEntityAttributeValueAuditRawValue.modifiedBy),
      created: new FormControl(trackedEntityAttributeValueAuditRawValue.created),
      updated: new FormControl(trackedEntityAttributeValueAuditRawValue.updated),
      auditType: new FormControl(trackedEntityAttributeValueAuditRawValue.auditType),
      attribute: new FormControl(trackedEntityAttributeValueAuditRawValue.attribute, {
        validators: [Validators.required],
      }),
      entityInstance: new FormControl(trackedEntityAttributeValueAuditRawValue.entityInstance, {
        validators: [Validators.required],
      }),
    });
  }

  getTrackedEntityAttributeValueAudit(
    form: TrackedEntityAttributeValueAuditFormGroup
  ): ITrackedEntityAttributeValueAudit | NewTrackedEntityAttributeValueAudit {
    return this.convertTrackedEntityAttributeValueAuditRawValueToTrackedEntityAttributeValueAudit(
      form.getRawValue() as TrackedEntityAttributeValueAuditFormRawValue | NewTrackedEntityAttributeValueAuditFormRawValue
    );
  }

  resetForm(
    form: TrackedEntityAttributeValueAuditFormGroup,
    trackedEntityAttributeValueAudit: TrackedEntityAttributeValueAuditFormGroupInput
  ): void {
    const trackedEntityAttributeValueAuditRawValue = this.convertTrackedEntityAttributeValueAuditToTrackedEntityAttributeValueAuditRawValue(
      { ...this.getFormDefaults(), ...trackedEntityAttributeValueAudit }
    );
    form.reset(
      {
        ...trackedEntityAttributeValueAuditRawValue,
        id: { value: trackedEntityAttributeValueAuditRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TrackedEntityAttributeValueAuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertTrackedEntityAttributeValueAuditRawValueToTrackedEntityAttributeValueAudit(
    rawTrackedEntityAttributeValueAudit: TrackedEntityAttributeValueAuditFormRawValue | NewTrackedEntityAttributeValueAuditFormRawValue
  ): ITrackedEntityAttributeValueAudit | NewTrackedEntityAttributeValueAudit {
    return {
      ...rawTrackedEntityAttributeValueAudit,
      created: dayjs(rawTrackedEntityAttributeValueAudit.created, DATE_TIME_FORMAT),
      updated: dayjs(rawTrackedEntityAttributeValueAudit.updated, DATE_TIME_FORMAT),
    };
  }

  private convertTrackedEntityAttributeValueAuditToTrackedEntityAttributeValueAuditRawValue(
    trackedEntityAttributeValueAudit:
      | ITrackedEntityAttributeValueAudit
      | (Partial<NewTrackedEntityAttributeValueAudit> & TrackedEntityAttributeValueAuditFormDefaults)
  ): TrackedEntityAttributeValueAuditFormRawValue | PartialWithRequiredKeyOf<NewTrackedEntityAttributeValueAuditFormRawValue> {
    return {
      ...trackedEntityAttributeValueAudit,
      created: trackedEntityAttributeValueAudit.created ? trackedEntityAttributeValueAudit.created.format(DATE_TIME_FORMAT) : undefined,
      updated: trackedEntityAttributeValueAudit.updated ? trackedEntityAttributeValueAudit.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
