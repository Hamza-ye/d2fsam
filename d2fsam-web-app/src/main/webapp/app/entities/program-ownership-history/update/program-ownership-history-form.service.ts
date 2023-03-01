import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramOwnershipHistory, NewProgramOwnershipHistory } from '../program-ownership-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramOwnershipHistory for edit and NewProgramOwnershipHistoryFormGroupInput for create.
 */
type ProgramOwnershipHistoryFormGroupInput = IProgramOwnershipHistory | PartialWithRequiredKeyOf<NewProgramOwnershipHistory>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramOwnershipHistory | NewProgramOwnershipHistory> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type ProgramOwnershipHistoryFormRawValue = FormValueOf<IProgramOwnershipHistory>;

type NewProgramOwnershipHistoryFormRawValue = FormValueOf<NewProgramOwnershipHistory>;

type ProgramOwnershipHistoryFormDefaults = Pick<NewProgramOwnershipHistory, 'id' | 'startDate' | 'endDate'>;

type ProgramOwnershipHistoryFormGroupContent = {
  id: FormControl<ProgramOwnershipHistoryFormRawValue['id'] | NewProgramOwnershipHistory['id']>;
  createdBy: FormControl<ProgramOwnershipHistoryFormRawValue['createdBy']>;
  startDate: FormControl<ProgramOwnershipHistoryFormRawValue['startDate']>;
  endDate: FormControl<ProgramOwnershipHistoryFormRawValue['endDate']>;
  program: FormControl<ProgramOwnershipHistoryFormRawValue['program']>;
  entityInstance: FormControl<ProgramOwnershipHistoryFormRawValue['entityInstance']>;
  organisationUnit: FormControl<ProgramOwnershipHistoryFormRawValue['organisationUnit']>;
};

export type ProgramOwnershipHistoryFormGroup = FormGroup<ProgramOwnershipHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramOwnershipHistoryFormService {
  createProgramOwnershipHistoryFormGroup(
    programOwnershipHistory: ProgramOwnershipHistoryFormGroupInput = { id: null }
  ): ProgramOwnershipHistoryFormGroup {
    const programOwnershipHistoryRawValue = this.convertProgramOwnershipHistoryToProgramOwnershipHistoryRawValue({
      ...this.getFormDefaults(),
      ...programOwnershipHistory,
    });
    return new FormGroup<ProgramOwnershipHistoryFormGroupContent>({
      id: new FormControl(
        { value: programOwnershipHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      createdBy: new FormControl(programOwnershipHistoryRawValue.createdBy),
      startDate: new FormControl(programOwnershipHistoryRawValue.startDate),
      endDate: new FormControl(programOwnershipHistoryRawValue.endDate),
      program: new FormControl(programOwnershipHistoryRawValue.program, {
        validators: [Validators.required],
      }),
      entityInstance: new FormControl(programOwnershipHistoryRawValue.entityInstance, {
        validators: [Validators.required],
      }),
      organisationUnit: new FormControl(programOwnershipHistoryRawValue.organisationUnit),
    });
  }

  getProgramOwnershipHistory(form: ProgramOwnershipHistoryFormGroup): IProgramOwnershipHistory | NewProgramOwnershipHistory {
    return this.convertProgramOwnershipHistoryRawValueToProgramOwnershipHistory(
      form.getRawValue() as ProgramOwnershipHistoryFormRawValue | NewProgramOwnershipHistoryFormRawValue
    );
  }

  resetForm(form: ProgramOwnershipHistoryFormGroup, programOwnershipHistory: ProgramOwnershipHistoryFormGroupInput): void {
    const programOwnershipHistoryRawValue = this.convertProgramOwnershipHistoryToProgramOwnershipHistoryRawValue({
      ...this.getFormDefaults(),
      ...programOwnershipHistory,
    });
    form.reset(
      {
        ...programOwnershipHistoryRawValue,
        id: { value: programOwnershipHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramOwnershipHistoryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertProgramOwnershipHistoryRawValueToProgramOwnershipHistory(
    rawProgramOwnershipHistory: ProgramOwnershipHistoryFormRawValue | NewProgramOwnershipHistoryFormRawValue
  ): IProgramOwnershipHistory | NewProgramOwnershipHistory {
    return {
      ...rawProgramOwnershipHistory,
      startDate: dayjs(rawProgramOwnershipHistory.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawProgramOwnershipHistory.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertProgramOwnershipHistoryToProgramOwnershipHistoryRawValue(
    programOwnershipHistory: IProgramOwnershipHistory | (Partial<NewProgramOwnershipHistory> & ProgramOwnershipHistoryFormDefaults)
  ): ProgramOwnershipHistoryFormRawValue | PartialWithRequiredKeyOf<NewProgramOwnershipHistoryFormRawValue> {
    return {
      ...programOwnershipHistory,
      startDate: programOwnershipHistory.startDate ? programOwnershipHistory.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: programOwnershipHistory.endDate ? programOwnershipHistory.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
