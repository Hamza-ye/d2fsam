import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramStageDataElement, NewProgramStageDataElement } from '../program-stage-data-element.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramStageDataElement for edit and NewProgramStageDataElementFormGroupInput for create.
 */
type ProgramStageDataElementFormGroupInput = IProgramStageDataElement | PartialWithRequiredKeyOf<NewProgramStageDataElement>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramStageDataElement | NewProgramStageDataElement> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ProgramStageDataElementFormRawValue = FormValueOf<IProgramStageDataElement>;

type NewProgramStageDataElementFormRawValue = FormValueOf<NewProgramStageDataElement>;

type ProgramStageDataElementFormDefaults = Pick<
  NewProgramStageDataElement,
  'id' | 'created' | 'updated' | 'compulsory' | 'allowProvidedElsewhere' | 'displayInReports' | 'allowFutureDate' | 'skipSynchronization'
>;

type ProgramStageDataElementFormGroupContent = {
  id: FormControl<ProgramStageDataElementFormRawValue['id'] | NewProgramStageDataElement['id']>;
  uid: FormControl<ProgramStageDataElementFormRawValue['uid']>;
  code: FormControl<ProgramStageDataElementFormRawValue['code']>;
  created: FormControl<ProgramStageDataElementFormRawValue['created']>;
  updated: FormControl<ProgramStageDataElementFormRawValue['updated']>;
  compulsory: FormControl<ProgramStageDataElementFormRawValue['compulsory']>;
  allowProvidedElsewhere: FormControl<ProgramStageDataElementFormRawValue['allowProvidedElsewhere']>;
  sortOrder: FormControl<ProgramStageDataElementFormRawValue['sortOrder']>;
  displayInReports: FormControl<ProgramStageDataElementFormRawValue['displayInReports']>;
  allowFutureDate: FormControl<ProgramStageDataElementFormRawValue['allowFutureDate']>;
  skipSynchronization: FormControl<ProgramStageDataElementFormRawValue['skipSynchronization']>;
  defaultValue: FormControl<ProgramStageDataElementFormRawValue['defaultValue']>;
  programStage: FormControl<ProgramStageDataElementFormRawValue['programStage']>;
  dataElement: FormControl<ProgramStageDataElementFormRawValue['dataElement']>;
  createdBy: FormControl<ProgramStageDataElementFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramStageDataElementFormRawValue['updatedBy']>;
};

export type ProgramStageDataElementFormGroup = FormGroup<ProgramStageDataElementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramStageDataElementFormService {
  createProgramStageDataElementFormGroup(
    programStageDataElement: ProgramStageDataElementFormGroupInput = { id: null }
  ): ProgramStageDataElementFormGroup {
    const programStageDataElementRawValue = this.convertProgramStageDataElementToProgramStageDataElementRawValue({
      ...this.getFormDefaults(),
      ...programStageDataElement,
    });
    return new FormGroup<ProgramStageDataElementFormGroupContent>({
      id: new FormControl(
        { value: programStageDataElementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programStageDataElementRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(programStageDataElementRawValue.code),
      created: new FormControl(programStageDataElementRawValue.created),
      updated: new FormControl(programStageDataElementRawValue.updated),
      compulsory: new FormControl(programStageDataElementRawValue.compulsory),
      allowProvidedElsewhere: new FormControl(programStageDataElementRawValue.allowProvidedElsewhere),
      sortOrder: new FormControl(programStageDataElementRawValue.sortOrder),
      displayInReports: new FormControl(programStageDataElementRawValue.displayInReports),
      allowFutureDate: new FormControl(programStageDataElementRawValue.allowFutureDate),
      skipSynchronization: new FormControl(programStageDataElementRawValue.skipSynchronization),
      defaultValue: new FormControl(programStageDataElementRawValue.defaultValue),
      programStage: new FormControl(programStageDataElementRawValue.programStage, {
        validators: [Validators.required],
      }),
      dataElement: new FormControl(programStageDataElementRawValue.dataElement, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(programStageDataElementRawValue.createdBy),
      updatedBy: new FormControl(programStageDataElementRawValue.updatedBy),
    });
  }

  getProgramStageDataElement(form: ProgramStageDataElementFormGroup): IProgramStageDataElement | NewProgramStageDataElement {
    return this.convertProgramStageDataElementRawValueToProgramStageDataElement(
      form.getRawValue() as ProgramStageDataElementFormRawValue | NewProgramStageDataElementFormRawValue
    );
  }

  resetForm(form: ProgramStageDataElementFormGroup, programStageDataElement: ProgramStageDataElementFormGroupInput): void {
    const programStageDataElementRawValue = this.convertProgramStageDataElementToProgramStageDataElementRawValue({
      ...this.getFormDefaults(),
      ...programStageDataElement,
    });
    form.reset(
      {
        ...programStageDataElementRawValue,
        id: { value: programStageDataElementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramStageDataElementFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      compulsory: false,
      allowProvidedElsewhere: false,
      displayInReports: false,
      allowFutureDate: false,
      skipSynchronization: false,
    };
  }

  private convertProgramStageDataElementRawValueToProgramStageDataElement(
    rawProgramStageDataElement: ProgramStageDataElementFormRawValue | NewProgramStageDataElementFormRawValue
  ): IProgramStageDataElement | NewProgramStageDataElement {
    return {
      ...rawProgramStageDataElement,
      created: dayjs(rawProgramStageDataElement.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgramStageDataElement.updated, DATE_TIME_FORMAT),
    };
  }

  private convertProgramStageDataElementToProgramStageDataElementRawValue(
    programStageDataElement: IProgramStageDataElement | (Partial<NewProgramStageDataElement> & ProgramStageDataElementFormDefaults)
  ): ProgramStageDataElementFormRawValue | PartialWithRequiredKeyOf<NewProgramStageDataElementFormRawValue> {
    return {
      ...programStageDataElement,
      created: programStageDataElement.created ? programStageDataElement.created.format(DATE_TIME_FORMAT) : undefined,
      updated: programStageDataElement.updated ? programStageDataElement.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
