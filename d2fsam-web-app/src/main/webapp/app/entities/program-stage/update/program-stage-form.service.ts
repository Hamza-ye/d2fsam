import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramStage, NewProgramStage } from '../program-stage.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramStage for edit and NewProgramStageFormGroupInput for create.
 */
type ProgramStageFormGroupInput = IProgramStage | PartialWithRequiredKeyOf<NewProgramStage>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramStage | NewProgramStage> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ProgramStageFormRawValue = FormValueOf<IProgramStage>;

type NewProgramStageFormRawValue = FormValueOf<NewProgramStage>;

type ProgramStageFormDefaults = Pick<
  NewProgramStage,
  | 'id'
  | 'created'
  | 'updated'
  | 'repeatable'
  | 'autoGenerateEvent'
  | 'blockEntryForm'
  | 'openAfterEnrollment'
  | 'generatedByEnrollmentDate'
  | 'hideDueDate'
  | 'enableUserAssignment'
  | 'enableTeamAssignment'
  | 'inactive'
>;

type ProgramStageFormGroupContent = {
  id: FormControl<ProgramStageFormRawValue['id'] | NewProgramStage['id']>;
  uid: FormControl<ProgramStageFormRawValue['uid']>;
  code: FormControl<ProgramStageFormRawValue['code']>;
  created: FormControl<ProgramStageFormRawValue['created']>;
  updated: FormControl<ProgramStageFormRawValue['updated']>;
  name: FormControl<ProgramStageFormRawValue['name']>;
  description: FormControl<ProgramStageFormRawValue['description']>;
  minDaysFromStart: FormControl<ProgramStageFormRawValue['minDaysFromStart']>;
  repeatable: FormControl<ProgramStageFormRawValue['repeatable']>;
  executionDateLabel: FormControl<ProgramStageFormRawValue['executionDateLabel']>;
  dueDateLabel: FormControl<ProgramStageFormRawValue['dueDateLabel']>;
  autoGenerateEvent: FormControl<ProgramStageFormRawValue['autoGenerateEvent']>;
  validationStrategy: FormControl<ProgramStageFormRawValue['validationStrategy']>;
  blockEntryForm: FormControl<ProgramStageFormRawValue['blockEntryForm']>;
  openAfterEnrollment: FormControl<ProgramStageFormRawValue['openAfterEnrollment']>;
  generatedByEnrollmentDate: FormControl<ProgramStageFormRawValue['generatedByEnrollmentDate']>;
  sortOrder: FormControl<ProgramStageFormRawValue['sortOrder']>;
  hideDueDate: FormControl<ProgramStageFormRawValue['hideDueDate']>;
  featureType: FormControl<ProgramStageFormRawValue['featureType']>;
  enableUserAssignment: FormControl<ProgramStageFormRawValue['enableUserAssignment']>;
  enableTeamAssignment: FormControl<ProgramStageFormRawValue['enableTeamAssignment']>;
  inactive: FormControl<ProgramStageFormRawValue['inactive']>;
  periodType: FormControl<ProgramStageFormRawValue['periodType']>;
  program: FormControl<ProgramStageFormRawValue['program']>;
  createdBy: FormControl<ProgramStageFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramStageFormRawValue['updatedBy']>;
};

export type ProgramStageFormGroup = FormGroup<ProgramStageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramStageFormService {
  createProgramStageFormGroup(programStage: ProgramStageFormGroupInput = { id: null }): ProgramStageFormGroup {
    const programStageRawValue = this.convertProgramStageToProgramStageRawValue({
      ...this.getFormDefaults(),
      ...programStage,
    });
    return new FormGroup<ProgramStageFormGroupContent>({
      id: new FormControl(
        { value: programStageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programStageRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(programStageRawValue.code),
      created: new FormControl(programStageRawValue.created),
      updated: new FormControl(programStageRawValue.updated),
      name: new FormControl(programStageRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(programStageRawValue.description),
      minDaysFromStart: new FormControl(programStageRawValue.minDaysFromStart),
      repeatable: new FormControl(programStageRawValue.repeatable),
      executionDateLabel: new FormControl(programStageRawValue.executionDateLabel),
      dueDateLabel: new FormControl(programStageRawValue.dueDateLabel),
      autoGenerateEvent: new FormControl(programStageRawValue.autoGenerateEvent),
      validationStrategy: new FormControl(programStageRawValue.validationStrategy),
      blockEntryForm: new FormControl(programStageRawValue.blockEntryForm),
      openAfterEnrollment: new FormControl(programStageRawValue.openAfterEnrollment),
      generatedByEnrollmentDate: new FormControl(programStageRawValue.generatedByEnrollmentDate),
      sortOrder: new FormControl(programStageRawValue.sortOrder),
      hideDueDate: new FormControl(programStageRawValue.hideDueDate),
      featureType: new FormControl(programStageRawValue.featureType),
      enableUserAssignment: new FormControl(programStageRawValue.enableUserAssignment),
      enableTeamAssignment: new FormControl(programStageRawValue.enableTeamAssignment),
      inactive: new FormControl(programStageRawValue.inactive),
      periodType: new FormControl(programStageRawValue.periodType),
      program: new FormControl(programStageRawValue.program),
      createdBy: new FormControl(programStageRawValue.createdBy),
      updatedBy: new FormControl(programStageRawValue.updatedBy),
    });
  }

  getProgramStage(form: ProgramStageFormGroup): IProgramStage | NewProgramStage {
    return this.convertProgramStageRawValueToProgramStage(form.getRawValue() as ProgramStageFormRawValue | NewProgramStageFormRawValue);
  }

  resetForm(form: ProgramStageFormGroup, programStage: ProgramStageFormGroupInput): void {
    const programStageRawValue = this.convertProgramStageToProgramStageRawValue({ ...this.getFormDefaults(), ...programStage });
    form.reset(
      {
        ...programStageRawValue,
        id: { value: programStageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramStageFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      repeatable: false,
      autoGenerateEvent: false,
      blockEntryForm: false,
      openAfterEnrollment: false,
      generatedByEnrollmentDate: false,
      hideDueDate: false,
      enableUserAssignment: false,
      enableTeamAssignment: false,
      inactive: false,
    };
  }

  private convertProgramStageRawValueToProgramStage(
    rawProgramStage: ProgramStageFormRawValue | NewProgramStageFormRawValue
  ): IProgramStage | NewProgramStage {
    return {
      ...rawProgramStage,
      created: dayjs(rawProgramStage.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgramStage.updated, DATE_TIME_FORMAT),
    };
  }

  private convertProgramStageToProgramStageRawValue(
    programStage: IProgramStage | (Partial<NewProgramStage> & ProgramStageFormDefaults)
  ): ProgramStageFormRawValue | PartialWithRequiredKeyOf<NewProgramStageFormRawValue> {
    return {
      ...programStage,
      created: programStage.created ? programStage.created.format(DATE_TIME_FORMAT) : undefined,
      updated: programStage.updated ? programStage.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
