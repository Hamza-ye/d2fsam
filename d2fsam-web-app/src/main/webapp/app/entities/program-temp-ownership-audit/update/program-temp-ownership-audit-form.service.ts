import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramTempOwnershipAudit, NewProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramTempOwnershipAudit for edit and NewProgramTempOwnershipAuditFormGroupInput for create.
 */
type ProgramTempOwnershipAuditFormGroupInput = IProgramTempOwnershipAudit | PartialWithRequiredKeyOf<NewProgramTempOwnershipAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramTempOwnershipAudit | NewProgramTempOwnershipAudit> = Omit<T, 'created'> & {
  created?: string | null;
};

type ProgramTempOwnershipAuditFormRawValue = FormValueOf<IProgramTempOwnershipAudit>;

type NewProgramTempOwnershipAuditFormRawValue = FormValueOf<NewProgramTempOwnershipAudit>;

type ProgramTempOwnershipAuditFormDefaults = Pick<NewProgramTempOwnershipAudit, 'id' | 'created'>;

type ProgramTempOwnershipAuditFormGroupContent = {
  id: FormControl<ProgramTempOwnershipAuditFormRawValue['id'] | NewProgramTempOwnershipAudit['id']>;
  reason: FormControl<ProgramTempOwnershipAuditFormRawValue['reason']>;
  created: FormControl<ProgramTempOwnershipAuditFormRawValue['created']>;
  accessedBy: FormControl<ProgramTempOwnershipAuditFormRawValue['accessedBy']>;
  program: FormControl<ProgramTempOwnershipAuditFormRawValue['program']>;
  entityInstance: FormControl<ProgramTempOwnershipAuditFormRawValue['entityInstance']>;
};

export type ProgramTempOwnershipAuditFormGroup = FormGroup<ProgramTempOwnershipAuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramTempOwnershipAuditFormService {
  createProgramTempOwnershipAuditFormGroup(
    programTempOwnershipAudit: ProgramTempOwnershipAuditFormGroupInput = { id: null }
  ): ProgramTempOwnershipAuditFormGroup {
    const programTempOwnershipAuditRawValue = this.convertProgramTempOwnershipAuditToProgramTempOwnershipAuditRawValue({
      ...this.getFormDefaults(),
      ...programTempOwnershipAudit,
    });
    return new FormGroup<ProgramTempOwnershipAuditFormGroupContent>({
      id: new FormControl(
        { value: programTempOwnershipAuditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      reason: new FormControl(programTempOwnershipAuditRawValue.reason, {
        validators: [Validators.maxLength(50000)],
      }),
      created: new FormControl(programTempOwnershipAuditRawValue.created),
      accessedBy: new FormControl(programTempOwnershipAuditRawValue.accessedBy),
      program: new FormControl(programTempOwnershipAuditRawValue.program, {
        validators: [Validators.required],
      }),
      entityInstance: new FormControl(programTempOwnershipAuditRawValue.entityInstance, {
        validators: [Validators.required],
      }),
    });
  }

  getProgramTempOwnershipAudit(form: ProgramTempOwnershipAuditFormGroup): IProgramTempOwnershipAudit | NewProgramTempOwnershipAudit {
    return this.convertProgramTempOwnershipAuditRawValueToProgramTempOwnershipAudit(
      form.getRawValue() as ProgramTempOwnershipAuditFormRawValue | NewProgramTempOwnershipAuditFormRawValue
    );
  }

  resetForm(form: ProgramTempOwnershipAuditFormGroup, programTempOwnershipAudit: ProgramTempOwnershipAuditFormGroupInput): void {
    const programTempOwnershipAuditRawValue = this.convertProgramTempOwnershipAuditToProgramTempOwnershipAuditRawValue({
      ...this.getFormDefaults(),
      ...programTempOwnershipAudit,
    });
    form.reset(
      {
        ...programTempOwnershipAuditRawValue,
        id: { value: programTempOwnershipAuditRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramTempOwnershipAuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
    };
  }

  private convertProgramTempOwnershipAuditRawValueToProgramTempOwnershipAudit(
    rawProgramTempOwnershipAudit: ProgramTempOwnershipAuditFormRawValue | NewProgramTempOwnershipAuditFormRawValue
  ): IProgramTempOwnershipAudit | NewProgramTempOwnershipAudit {
    return {
      ...rawProgramTempOwnershipAudit,
      created: dayjs(rawProgramTempOwnershipAudit.created, DATE_TIME_FORMAT),
    };
  }

  private convertProgramTempOwnershipAuditToProgramTempOwnershipAuditRawValue(
    programTempOwnershipAudit: IProgramTempOwnershipAudit | (Partial<NewProgramTempOwnershipAudit> & ProgramTempOwnershipAuditFormDefaults)
  ): ProgramTempOwnershipAuditFormRawValue | PartialWithRequiredKeyOf<NewProgramTempOwnershipAuditFormRawValue> {
    return {
      ...programTempOwnershipAudit,
      created: programTempOwnershipAudit.created ? programTempOwnershipAudit.created.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
