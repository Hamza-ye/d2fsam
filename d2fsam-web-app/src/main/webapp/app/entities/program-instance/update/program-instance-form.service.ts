import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramInstance, NewProgramInstance } from '../program-instance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramInstance for edit and NewProgramInstanceFormGroupInput for create.
 */
type ProgramInstanceFormGroupInput = IProgramInstance | PartialWithRequiredKeyOf<NewProgramInstance>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramInstance | NewProgramInstance> = Omit<
  T,
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'lastSynchronized'
  | 'incidentDate'
  | 'enrollmentDate'
  | 'endDate'
  | 'completedDate'
  | 'deletedAt'
> & {
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  lastSynchronized?: string | null;
  incidentDate?: string | null;
  enrollmentDate?: string | null;
  endDate?: string | null;
  completedDate?: string | null;
  deletedAt?: string | null;
};

type ProgramInstanceFormRawValue = FormValueOf<IProgramInstance>;

type NewProgramInstanceFormRawValue = FormValueOf<NewProgramInstance>;

type ProgramInstanceFormDefaults = Pick<
  NewProgramInstance,
  | 'id'
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'lastSynchronized'
  | 'incidentDate'
  | 'enrollmentDate'
  | 'endDate'
  | 'completedDate'
  | 'followup'
  | 'deleted'
  | 'deletedAt'
  | 'comments'
>;

type ProgramInstanceFormGroupContent = {
  id: FormControl<ProgramInstanceFormRawValue['id'] | NewProgramInstance['id']>;
  uid: FormControl<ProgramInstanceFormRawValue['uid']>;
  uuid: FormControl<ProgramInstanceFormRawValue['uuid']>;
  created: FormControl<ProgramInstanceFormRawValue['created']>;
  updated: FormControl<ProgramInstanceFormRawValue['updated']>;
  createdAtClient: FormControl<ProgramInstanceFormRawValue['createdAtClient']>;
  updatedAtClient: FormControl<ProgramInstanceFormRawValue['updatedAtClient']>;
  lastSynchronized: FormControl<ProgramInstanceFormRawValue['lastSynchronized']>;
  incidentDate: FormControl<ProgramInstanceFormRawValue['incidentDate']>;
  enrollmentDate: FormControl<ProgramInstanceFormRawValue['enrollmentDate']>;
  periodLabel: FormControl<ProgramInstanceFormRawValue['periodLabel']>;
  endDate: FormControl<ProgramInstanceFormRawValue['endDate']>;
  status: FormControl<ProgramInstanceFormRawValue['status']>;
  storedBy: FormControl<ProgramInstanceFormRawValue['storedBy']>;
  completedBy: FormControl<ProgramInstanceFormRawValue['completedBy']>;
  completedDate: FormControl<ProgramInstanceFormRawValue['completedDate']>;
  followup: FormControl<ProgramInstanceFormRawValue['followup']>;
  deleted: FormControl<ProgramInstanceFormRawValue['deleted']>;
  deletedAt: FormControl<ProgramInstanceFormRawValue['deletedAt']>;
  entityInstance: FormControl<ProgramInstanceFormRawValue['entityInstance']>;
  program: FormControl<ProgramInstanceFormRawValue['program']>;
  organisationUnit: FormControl<ProgramInstanceFormRawValue['organisationUnit']>;
  activity: FormControl<ProgramInstanceFormRawValue['activity']>;
  createdBy: FormControl<ProgramInstanceFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramInstanceFormRawValue['updatedBy']>;
  approvedBy: FormControl<ProgramInstanceFormRawValue['approvedBy']>;
  comments: FormControl<ProgramInstanceFormRawValue['comments']>;
};

export type ProgramInstanceFormGroup = FormGroup<ProgramInstanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramInstanceFormService {
  createProgramInstanceFormGroup(programInstance: ProgramInstanceFormGroupInput = { id: null }): ProgramInstanceFormGroup {
    const programInstanceRawValue = this.convertProgramInstanceToProgramInstanceRawValue({
      ...this.getFormDefaults(),
      ...programInstance,
    });
    return new FormGroup<ProgramInstanceFormGroupContent>({
      id: new FormControl(
        { value: programInstanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programInstanceRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      uuid: new FormControl(programInstanceRawValue.uuid),
      created: new FormControl(programInstanceRawValue.created),
      updated: new FormControl(programInstanceRawValue.updated),
      createdAtClient: new FormControl(programInstanceRawValue.createdAtClient),
      updatedAtClient: new FormControl(programInstanceRawValue.updatedAtClient),
      lastSynchronized: new FormControl(programInstanceRawValue.lastSynchronized),
      incidentDate: new FormControl(programInstanceRawValue.incidentDate),
      enrollmentDate: new FormControl(programInstanceRawValue.enrollmentDate),
      periodLabel: new FormControl(programInstanceRawValue.periodLabel),
      endDate: new FormControl(programInstanceRawValue.endDate),
      status: new FormControl(programInstanceRawValue.status),
      storedBy: new FormControl(programInstanceRawValue.storedBy),
      completedBy: new FormControl(programInstanceRawValue.completedBy),
      completedDate: new FormControl(programInstanceRawValue.completedDate),
      followup: new FormControl(programInstanceRawValue.followup),
      deleted: new FormControl(programInstanceRawValue.deleted),
      deletedAt: new FormControl(programInstanceRawValue.deletedAt),
      entityInstance: new FormControl(programInstanceRawValue.entityInstance, {
        validators: [Validators.required],
      }),
      program: new FormControl(programInstanceRawValue.program, {
        validators: [Validators.required],
      }),
      organisationUnit: new FormControl(programInstanceRawValue.organisationUnit, {
        validators: [Validators.required],
      }),
      activity: new FormControl(programInstanceRawValue.activity, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(programInstanceRawValue.createdBy),
      updatedBy: new FormControl(programInstanceRawValue.updatedBy),
      approvedBy: new FormControl(programInstanceRawValue.approvedBy),
      comments: new FormControl(programInstanceRawValue.comments ?? []),
    });
  }

  getProgramInstance(form: ProgramInstanceFormGroup): IProgramInstance | NewProgramInstance {
    return this.convertProgramInstanceRawValueToProgramInstance(
      form.getRawValue() as ProgramInstanceFormRawValue | NewProgramInstanceFormRawValue
    );
  }

  resetForm(form: ProgramInstanceFormGroup, programInstance: ProgramInstanceFormGroupInput): void {
    const programInstanceRawValue = this.convertProgramInstanceToProgramInstanceRawValue({ ...this.getFormDefaults(), ...programInstance });
    form.reset(
      {
        ...programInstanceRawValue,
        id: { value: programInstanceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramInstanceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      createdAtClient: currentTime,
      updatedAtClient: currentTime,
      lastSynchronized: currentTime,
      incidentDate: currentTime,
      enrollmentDate: currentTime,
      endDate: currentTime,
      completedDate: currentTime,
      followup: false,
      deleted: false,
      deletedAt: currentTime,
      comments: [],
    };
  }

  private convertProgramInstanceRawValueToProgramInstance(
    rawProgramInstance: ProgramInstanceFormRawValue | NewProgramInstanceFormRawValue
  ): IProgramInstance | NewProgramInstance {
    return {
      ...rawProgramInstance,
      created: dayjs(rawProgramInstance.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgramInstance.updated, DATE_TIME_FORMAT),
      createdAtClient: dayjs(rawProgramInstance.createdAtClient, DATE_TIME_FORMAT),
      updatedAtClient: dayjs(rawProgramInstance.updatedAtClient, DATE_TIME_FORMAT),
      lastSynchronized: dayjs(rawProgramInstance.lastSynchronized, DATE_TIME_FORMAT),
      incidentDate: dayjs(rawProgramInstance.incidentDate, DATE_TIME_FORMAT),
      enrollmentDate: dayjs(rawProgramInstance.enrollmentDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawProgramInstance.endDate, DATE_TIME_FORMAT),
      completedDate: dayjs(rawProgramInstance.completedDate, DATE_TIME_FORMAT),
      deletedAt: dayjs(rawProgramInstance.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertProgramInstanceToProgramInstanceRawValue(
    programInstance: IProgramInstance | (Partial<NewProgramInstance> & ProgramInstanceFormDefaults)
  ): ProgramInstanceFormRawValue | PartialWithRequiredKeyOf<NewProgramInstanceFormRawValue> {
    return {
      ...programInstance,
      created: programInstance.created ? programInstance.created.format(DATE_TIME_FORMAT) : undefined,
      updated: programInstance.updated ? programInstance.updated.format(DATE_TIME_FORMAT) : undefined,
      createdAtClient: programInstance.createdAtClient ? programInstance.createdAtClient.format(DATE_TIME_FORMAT) : undefined,
      updatedAtClient: programInstance.updatedAtClient ? programInstance.updatedAtClient.format(DATE_TIME_FORMAT) : undefined,
      lastSynchronized: programInstance.lastSynchronized ? programInstance.lastSynchronized.format(DATE_TIME_FORMAT) : undefined,
      incidentDate: programInstance.incidentDate ? programInstance.incidentDate.format(DATE_TIME_FORMAT) : undefined,
      enrollmentDate: programInstance.enrollmentDate ? programInstance.enrollmentDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: programInstance.endDate ? programInstance.endDate.format(DATE_TIME_FORMAT) : undefined,
      completedDate: programInstance.completedDate ? programInstance.completedDate.format(DATE_TIME_FORMAT) : undefined,
      deletedAt: programInstance.deletedAt ? programInstance.deletedAt.format(DATE_TIME_FORMAT) : undefined,
      comments: programInstance.comments ?? [],
    };
  }
}
