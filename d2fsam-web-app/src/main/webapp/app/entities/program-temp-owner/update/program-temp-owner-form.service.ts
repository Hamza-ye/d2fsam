import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramTempOwner, NewProgramTempOwner } from '../program-temp-owner.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramTempOwner for edit and NewProgramTempOwnerFormGroupInput for create.
 */
type ProgramTempOwnerFormGroupInput = IProgramTempOwner | PartialWithRequiredKeyOf<NewProgramTempOwner>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramTempOwner | NewProgramTempOwner> = Omit<T, 'validTill'> & {
  validTill?: string | null;
};

type ProgramTempOwnerFormRawValue = FormValueOf<IProgramTempOwner>;

type NewProgramTempOwnerFormRawValue = FormValueOf<NewProgramTempOwner>;

type ProgramTempOwnerFormDefaults = Pick<NewProgramTempOwner, 'id' | 'validTill'>;

type ProgramTempOwnerFormGroupContent = {
  id: FormControl<ProgramTempOwnerFormRawValue['id'] | NewProgramTempOwner['id']>;
  reason: FormControl<ProgramTempOwnerFormRawValue['reason']>;
  validTill: FormControl<ProgramTempOwnerFormRawValue['validTill']>;
  program: FormControl<ProgramTempOwnerFormRawValue['program']>;
  entityInstance: FormControl<ProgramTempOwnerFormRawValue['entityInstance']>;
  user: FormControl<ProgramTempOwnerFormRawValue['user']>;
};

export type ProgramTempOwnerFormGroup = FormGroup<ProgramTempOwnerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramTempOwnerFormService {
  createProgramTempOwnerFormGroup(programTempOwner: ProgramTempOwnerFormGroupInput = { id: null }): ProgramTempOwnerFormGroup {
    const programTempOwnerRawValue = this.convertProgramTempOwnerToProgramTempOwnerRawValue({
      ...this.getFormDefaults(),
      ...programTempOwner,
    });
    return new FormGroup<ProgramTempOwnerFormGroupContent>({
      id: new FormControl(
        { value: programTempOwnerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      reason: new FormControl(programTempOwnerRawValue.reason, {
        validators: [Validators.maxLength(50000)],
      }),
      validTill: new FormControl(programTempOwnerRawValue.validTill),
      program: new FormControl(programTempOwnerRawValue.program, {
        validators: [Validators.required],
      }),
      entityInstance: new FormControl(programTempOwnerRawValue.entityInstance, {
        validators: [Validators.required],
      }),
      user: new FormControl(programTempOwnerRawValue.user),
    });
  }

  getProgramTempOwner(form: ProgramTempOwnerFormGroup): IProgramTempOwner | NewProgramTempOwner {
    return this.convertProgramTempOwnerRawValueToProgramTempOwner(
      form.getRawValue() as ProgramTempOwnerFormRawValue | NewProgramTempOwnerFormRawValue
    );
  }

  resetForm(form: ProgramTempOwnerFormGroup, programTempOwner: ProgramTempOwnerFormGroupInput): void {
    const programTempOwnerRawValue = this.convertProgramTempOwnerToProgramTempOwnerRawValue({
      ...this.getFormDefaults(),
      ...programTempOwner,
    });
    form.reset(
      {
        ...programTempOwnerRawValue,
        id: { value: programTempOwnerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramTempOwnerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      validTill: currentTime,
    };
  }

  private convertProgramTempOwnerRawValueToProgramTempOwner(
    rawProgramTempOwner: ProgramTempOwnerFormRawValue | NewProgramTempOwnerFormRawValue
  ): IProgramTempOwner | NewProgramTempOwner {
    return {
      ...rawProgramTempOwner,
      validTill: dayjs(rawProgramTempOwner.validTill, DATE_TIME_FORMAT),
    };
  }

  private convertProgramTempOwnerToProgramTempOwnerRawValue(
    programTempOwner: IProgramTempOwner | (Partial<NewProgramTempOwner> & ProgramTempOwnerFormDefaults)
  ): ProgramTempOwnerFormRawValue | PartialWithRequiredKeyOf<NewProgramTempOwnerFormRawValue> {
    return {
      ...programTempOwner,
      validTill: programTempOwner.validTill ? programTempOwner.validTill.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
