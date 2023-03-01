import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramStageInstanceFilter, NewProgramStageInstanceFilter } from '../program-stage-instance-filter.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramStageInstanceFilter for edit and NewProgramStageInstanceFilterFormGroupInput for create.
 */
type ProgramStageInstanceFilterFormGroupInput = IProgramStageInstanceFilter | PartialWithRequiredKeyOf<NewProgramStageInstanceFilter>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramStageInstanceFilter | NewProgramStageInstanceFilter> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ProgramStageInstanceFilterFormRawValue = FormValueOf<IProgramStageInstanceFilter>;

type NewProgramStageInstanceFilterFormRawValue = FormValueOf<NewProgramStageInstanceFilter>;

type ProgramStageInstanceFilterFormDefaults = Pick<NewProgramStageInstanceFilter, 'id' | 'created' | 'updated'>;

type ProgramStageInstanceFilterFormGroupContent = {
  id: FormControl<ProgramStageInstanceFilterFormRawValue['id'] | NewProgramStageInstanceFilter['id']>;
  uid: FormControl<ProgramStageInstanceFilterFormRawValue['uid']>;
  code: FormControl<ProgramStageInstanceFilterFormRawValue['code']>;
  name: FormControl<ProgramStageInstanceFilterFormRawValue['name']>;
  created: FormControl<ProgramStageInstanceFilterFormRawValue['created']>;
  updated: FormControl<ProgramStageInstanceFilterFormRawValue['updated']>;
  description: FormControl<ProgramStageInstanceFilterFormRawValue['description']>;
  program: FormControl<ProgramStageInstanceFilterFormRawValue['program']>;
  programStage: FormControl<ProgramStageInstanceFilterFormRawValue['programStage']>;
  createdBy: FormControl<ProgramStageInstanceFilterFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramStageInstanceFilterFormRawValue['updatedBy']>;
};

export type ProgramStageInstanceFilterFormGroup = FormGroup<ProgramStageInstanceFilterFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramStageInstanceFilterFormService {
  createProgramStageInstanceFilterFormGroup(
    programStageInstanceFilter: ProgramStageInstanceFilterFormGroupInput = { id: null }
  ): ProgramStageInstanceFilterFormGroup {
    const programStageInstanceFilterRawValue = this.convertProgramStageInstanceFilterToProgramStageInstanceFilterRawValue({
      ...this.getFormDefaults(),
      ...programStageInstanceFilter,
    });
    return new FormGroup<ProgramStageInstanceFilterFormGroupContent>({
      id: new FormControl(
        { value: programStageInstanceFilterRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programStageInstanceFilterRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(programStageInstanceFilterRawValue.code),
      name: new FormControl(programStageInstanceFilterRawValue.name),
      created: new FormControl(programStageInstanceFilterRawValue.created),
      updated: new FormControl(programStageInstanceFilterRawValue.updated),
      description: new FormControl(programStageInstanceFilterRawValue.description),
      program: new FormControl(programStageInstanceFilterRawValue.program),
      programStage: new FormControl(programStageInstanceFilterRawValue.programStage),
      createdBy: new FormControl(programStageInstanceFilterRawValue.createdBy),
      updatedBy: new FormControl(programStageInstanceFilterRawValue.updatedBy),
    });
  }

  getProgramStageInstanceFilter(form: ProgramStageInstanceFilterFormGroup): IProgramStageInstanceFilter | NewProgramStageInstanceFilter {
    return this.convertProgramStageInstanceFilterRawValueToProgramStageInstanceFilter(
      form.getRawValue() as ProgramStageInstanceFilterFormRawValue | NewProgramStageInstanceFilterFormRawValue
    );
  }

  resetForm(form: ProgramStageInstanceFilterFormGroup, programStageInstanceFilter: ProgramStageInstanceFilterFormGroupInput): void {
    const programStageInstanceFilterRawValue = this.convertProgramStageInstanceFilterToProgramStageInstanceFilterRawValue({
      ...this.getFormDefaults(),
      ...programStageInstanceFilter,
    });
    form.reset(
      {
        ...programStageInstanceFilterRawValue,
        id: { value: programStageInstanceFilterRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramStageInstanceFilterFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertProgramStageInstanceFilterRawValueToProgramStageInstanceFilter(
    rawProgramStageInstanceFilter: ProgramStageInstanceFilterFormRawValue | NewProgramStageInstanceFilterFormRawValue
  ): IProgramStageInstanceFilter | NewProgramStageInstanceFilter {
    return {
      ...rawProgramStageInstanceFilter,
      created: dayjs(rawProgramStageInstanceFilter.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgramStageInstanceFilter.updated, DATE_TIME_FORMAT),
    };
  }

  private convertProgramStageInstanceFilterToProgramStageInstanceFilterRawValue(
    programStageInstanceFilter:
      | IProgramStageInstanceFilter
      | (Partial<NewProgramStageInstanceFilter> & ProgramStageInstanceFilterFormDefaults)
  ): ProgramStageInstanceFilterFormRawValue | PartialWithRequiredKeyOf<NewProgramStageInstanceFilterFormRawValue> {
    return {
      ...programStageInstanceFilter,
      created: programStageInstanceFilter.created ? programStageInstanceFilter.created.format(DATE_TIME_FORMAT) : undefined,
      updated: programStageInstanceFilter.updated ? programStageInstanceFilter.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
