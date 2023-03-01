import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgram, NewProgram } from '../program.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgram for edit and NewProgramFormGroupInput for create.
 */
type ProgramFormGroupInput = IProgram | PartialWithRequiredKeyOf<NewProgram>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgram | NewProgram> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ProgramFormRawValue = FormValueOf<IProgram>;

type NewProgramFormRawValue = FormValueOf<NewProgram>;

type ProgramFormDefaults = Pick<
  NewProgram,
  | 'id'
  | 'created'
  | 'updated'
  | 'displayIncidentDate'
  | 'onlyEnrollOnce'
  | 'captureCoordinates'
  | 'ignoreOverDueEvents'
  | 'selectEnrollmentDatesInFuture'
  | 'selectIncidentDatesInFuture'
  | 'inactive'
  | 'organisationUnits'
>;

type ProgramFormGroupContent = {
  id: FormControl<ProgramFormRawValue['id'] | NewProgram['id']>;
  uid: FormControl<ProgramFormRawValue['uid']>;
  code: FormControl<ProgramFormRawValue['code']>;
  created: FormControl<ProgramFormRawValue['created']>;
  updated: FormControl<ProgramFormRawValue['updated']>;
  name: FormControl<ProgramFormRawValue['name']>;
  shortName: FormControl<ProgramFormRawValue['shortName']>;
  description: FormControl<ProgramFormRawValue['description']>;
  version: FormControl<ProgramFormRawValue['version']>;
  incidentDateLabel: FormControl<ProgramFormRawValue['incidentDateLabel']>;
  programType: FormControl<ProgramFormRawValue['programType']>;
  displayIncidentDate: FormControl<ProgramFormRawValue['displayIncidentDate']>;
  onlyEnrollOnce: FormControl<ProgramFormRawValue['onlyEnrollOnce']>;
  captureCoordinates: FormControl<ProgramFormRawValue['captureCoordinates']>;
  expiryDays: FormControl<ProgramFormRawValue['expiryDays']>;
  completeEventsExpiryDays: FormControl<ProgramFormRawValue['completeEventsExpiryDays']>;
  accessLevel: FormControl<ProgramFormRawValue['accessLevel']>;
  ignoreOverDueEvents: FormControl<ProgramFormRawValue['ignoreOverDueEvents']>;
  selectEnrollmentDatesInFuture: FormControl<ProgramFormRawValue['selectEnrollmentDatesInFuture']>;
  selectIncidentDatesInFuture: FormControl<ProgramFormRawValue['selectIncidentDatesInFuture']>;
  featureType: FormControl<ProgramFormRawValue['featureType']>;
  inactive: FormControl<ProgramFormRawValue['inactive']>;
  period: FormControl<ProgramFormRawValue['period']>;
  expiryPeriodType: FormControl<ProgramFormRawValue['expiryPeriodType']>;
  relatedProgram: FormControl<ProgramFormRawValue['relatedProgram']>;
  trackedEntityType: FormControl<ProgramFormRawValue['trackedEntityType']>;
  createdBy: FormControl<ProgramFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramFormRawValue['updatedBy']>;
  organisationUnits: FormControl<ProgramFormRawValue['organisationUnits']>;
};

export type ProgramFormGroup = FormGroup<ProgramFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramFormService {
  createProgramFormGroup(program: ProgramFormGroupInput = { id: null }): ProgramFormGroup {
    const programRawValue = this.convertProgramToProgramRawValue({
      ...this.getFormDefaults(),
      ...program,
    });
    return new FormGroup<ProgramFormGroupContent>({
      id: new FormControl(
        { value: programRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(programRawValue.code),
      created: new FormControl(programRawValue.created),
      updated: new FormControl(programRawValue.updated),
      name: new FormControl(programRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(programRawValue.shortName),
      description: new FormControl(programRawValue.description),
      version: new FormControl(programRawValue.version),
      incidentDateLabel: new FormControl(programRawValue.incidentDateLabel),
      programType: new FormControl(programRawValue.programType),
      displayIncidentDate: new FormControl(programRawValue.displayIncidentDate),
      onlyEnrollOnce: new FormControl(programRawValue.onlyEnrollOnce),
      captureCoordinates: new FormControl(programRawValue.captureCoordinates),
      expiryDays: new FormControl(programRawValue.expiryDays),
      completeEventsExpiryDays: new FormControl(programRawValue.completeEventsExpiryDays),
      accessLevel: new FormControl(programRawValue.accessLevel),
      ignoreOverDueEvents: new FormControl(programRawValue.ignoreOverDueEvents),
      selectEnrollmentDatesInFuture: new FormControl(programRawValue.selectEnrollmentDatesInFuture),
      selectIncidentDatesInFuture: new FormControl(programRawValue.selectIncidentDatesInFuture),
      featureType: new FormControl(programRawValue.featureType),
      inactive: new FormControl(programRawValue.inactive),
      period: new FormControl(programRawValue.period),
      expiryPeriodType: new FormControl(programRawValue.expiryPeriodType),
      relatedProgram: new FormControl(programRawValue.relatedProgram),
      trackedEntityType: new FormControl(programRawValue.trackedEntityType, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(programRawValue.createdBy),
      updatedBy: new FormControl(programRawValue.updatedBy),
      organisationUnits: new FormControl(programRawValue.organisationUnits ?? []),
    });
  }

  getProgram(form: ProgramFormGroup): IProgram | NewProgram {
    return this.convertProgramRawValueToProgram(form.getRawValue() as ProgramFormRawValue | NewProgramFormRawValue);
  }

  resetForm(form: ProgramFormGroup, program: ProgramFormGroupInput): void {
    const programRawValue = this.convertProgramToProgramRawValue({ ...this.getFormDefaults(), ...program });
    form.reset(
      {
        ...programRawValue,
        id: { value: programRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      displayIncidentDate: false,
      onlyEnrollOnce: false,
      captureCoordinates: false,
      ignoreOverDueEvents: false,
      selectEnrollmentDatesInFuture: false,
      selectIncidentDatesInFuture: false,
      inactive: false,
      organisationUnits: [],
    };
  }

  private convertProgramRawValueToProgram(rawProgram: ProgramFormRawValue | NewProgramFormRawValue): IProgram | NewProgram {
    return {
      ...rawProgram,
      created: dayjs(rawProgram.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgram.updated, DATE_TIME_FORMAT),
    };
  }

  private convertProgramToProgramRawValue(
    program: IProgram | (Partial<NewProgram> & ProgramFormDefaults)
  ): ProgramFormRawValue | PartialWithRequiredKeyOf<NewProgramFormRawValue> {
    return {
      ...program,
      created: program.created ? program.created.format(DATE_TIME_FORMAT) : undefined,
      updated: program.updated ? program.updated.format(DATE_TIME_FORMAT) : undefined,
      organisationUnits: program.organisationUnits ?? [],
    };
  }
}
