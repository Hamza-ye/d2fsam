import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProgramStageInstance, NewProgramStageInstance } from '../program-stage-instance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgramStageInstance for edit and NewProgramStageInstanceFormGroupInput for create.
 */
type ProgramStageInstanceFormGroupInput = IProgramStageInstance | PartialWithRequiredKeyOf<NewProgramStageInstance>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProgramStageInstance | NewProgramStageInstance> = Omit<
  T,
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'lastSynchronized'
  | 'dueDate'
  | 'executionDate'
  | 'completedDate'
  | 'deletedAt'
> & {
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  lastSynchronized?: string | null;
  dueDate?: string | null;
  executionDate?: string | null;
  completedDate?: string | null;
  deletedAt?: string | null;
};

type ProgramStageInstanceFormRawValue = FormValueOf<IProgramStageInstance>;

type NewProgramStageInstanceFormRawValue = FormValueOf<NewProgramStageInstance>;

type ProgramStageInstanceFormDefaults = Pick<
  NewProgramStageInstance,
  | 'id'
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'lastSynchronized'
  | 'dueDate'
  | 'executionDate'
  | 'completedDate'
  | 'deleted'
  | 'deletedAt'
  | 'comments'
>;

type ProgramStageInstanceFormGroupContent = {
  id: FormControl<ProgramStageInstanceFormRawValue['id'] | NewProgramStageInstance['id']>;
  uid: FormControl<ProgramStageInstanceFormRawValue['uid']>;
  uuid: FormControl<ProgramStageInstanceFormRawValue['uuid']>;
  code: FormControl<ProgramStageInstanceFormRawValue['code']>;
  created: FormControl<ProgramStageInstanceFormRawValue['created']>;
  updated: FormControl<ProgramStageInstanceFormRawValue['updated']>;
  createdAtClient: FormControl<ProgramStageInstanceFormRawValue['createdAtClient']>;
  updatedAtClient: FormControl<ProgramStageInstanceFormRawValue['updatedAtClient']>;
  lastSynchronized: FormControl<ProgramStageInstanceFormRawValue['lastSynchronized']>;
  dueDate: FormControl<ProgramStageInstanceFormRawValue['dueDate']>;
  executionDate: FormControl<ProgramStageInstanceFormRawValue['executionDate']>;
  status: FormControl<ProgramStageInstanceFormRawValue['status']>;
  storedBy: FormControl<ProgramStageInstanceFormRawValue['storedBy']>;
  completedBy: FormControl<ProgramStageInstanceFormRawValue['completedBy']>;
  completedDate: FormControl<ProgramStageInstanceFormRawValue['completedDate']>;
  deleted: FormControl<ProgramStageInstanceFormRawValue['deleted']>;
  deletedAt: FormControl<ProgramStageInstanceFormRawValue['deletedAt']>;
  programInstance: FormControl<ProgramStageInstanceFormRawValue['programInstance']>;
  programStage: FormControl<ProgramStageInstanceFormRawValue['programStage']>;
  organisationUnit: FormControl<ProgramStageInstanceFormRawValue['organisationUnit']>;
  assignedUser: FormControl<ProgramStageInstanceFormRawValue['assignedUser']>;
  period: FormControl<ProgramStageInstanceFormRawValue['period']>;
  createdBy: FormControl<ProgramStageInstanceFormRawValue['createdBy']>;
  updatedBy: FormControl<ProgramStageInstanceFormRawValue['updatedBy']>;
  approvedBy: FormControl<ProgramStageInstanceFormRawValue['approvedBy']>;
  comments: FormControl<ProgramStageInstanceFormRawValue['comments']>;
};

export type ProgramStageInstanceFormGroup = FormGroup<ProgramStageInstanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgramStageInstanceFormService {
  createProgramStageInstanceFormGroup(
    programStageInstance: ProgramStageInstanceFormGroupInput = { id: null }
  ): ProgramStageInstanceFormGroup {
    const programStageInstanceRawValue = this.convertProgramStageInstanceToProgramStageInstanceRawValue({
      ...this.getFormDefaults(),
      ...programStageInstance,
    });
    return new FormGroup<ProgramStageInstanceFormGroupContent>({
      id: new FormControl(
        { value: programStageInstanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(programStageInstanceRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      uuid: new FormControl(programStageInstanceRawValue.uuid),
      code: new FormControl(programStageInstanceRawValue.code),
      created: new FormControl(programStageInstanceRawValue.created),
      updated: new FormControl(programStageInstanceRawValue.updated),
      createdAtClient: new FormControl(programStageInstanceRawValue.createdAtClient),
      updatedAtClient: new FormControl(programStageInstanceRawValue.updatedAtClient),
      lastSynchronized: new FormControl(programStageInstanceRawValue.lastSynchronized),
      dueDate: new FormControl(programStageInstanceRawValue.dueDate),
      executionDate: new FormControl(programStageInstanceRawValue.executionDate),
      status: new FormControl(programStageInstanceRawValue.status),
      storedBy: new FormControl(programStageInstanceRawValue.storedBy),
      completedBy: new FormControl(programStageInstanceRawValue.completedBy),
      completedDate: new FormControl(programStageInstanceRawValue.completedDate),
      deleted: new FormControl(programStageInstanceRawValue.deleted),
      deletedAt: new FormControl(programStageInstanceRawValue.deletedAt),
      programInstance: new FormControl(programStageInstanceRawValue.programInstance),
      programStage: new FormControl(programStageInstanceRawValue.programStage, {
        validators: [Validators.required],
      }),
      organisationUnit: new FormControl(programStageInstanceRawValue.organisationUnit),
      assignedUser: new FormControl(programStageInstanceRawValue.assignedUser),
      period: new FormControl(programStageInstanceRawValue.period),
      createdBy: new FormControl(programStageInstanceRawValue.createdBy),
      updatedBy: new FormControl(programStageInstanceRawValue.updatedBy),
      approvedBy: new FormControl(programStageInstanceRawValue.approvedBy),
      comments: new FormControl(programStageInstanceRawValue.comments ?? []),
    });
  }

  getProgramStageInstance(form: ProgramStageInstanceFormGroup): IProgramStageInstance | NewProgramStageInstance {
    return this.convertProgramStageInstanceRawValueToProgramStageInstance(
      form.getRawValue() as ProgramStageInstanceFormRawValue | NewProgramStageInstanceFormRawValue
    );
  }

  resetForm(form: ProgramStageInstanceFormGroup, programStageInstance: ProgramStageInstanceFormGroupInput): void {
    const programStageInstanceRawValue = this.convertProgramStageInstanceToProgramStageInstanceRawValue({
      ...this.getFormDefaults(),
      ...programStageInstance,
    });
    form.reset(
      {
        ...programStageInstanceRawValue,
        id: { value: programStageInstanceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProgramStageInstanceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      createdAtClient: currentTime,
      updatedAtClient: currentTime,
      lastSynchronized: currentTime,
      dueDate: currentTime,
      executionDate: currentTime,
      completedDate: currentTime,
      deleted: false,
      deletedAt: currentTime,
      comments: [],
    };
  }

  private convertProgramStageInstanceRawValueToProgramStageInstance(
    rawProgramStageInstance: ProgramStageInstanceFormRawValue | NewProgramStageInstanceFormRawValue
  ): IProgramStageInstance | NewProgramStageInstance {
    return {
      ...rawProgramStageInstance,
      created: dayjs(rawProgramStageInstance.created, DATE_TIME_FORMAT),
      updated: dayjs(rawProgramStageInstance.updated, DATE_TIME_FORMAT),
      createdAtClient: dayjs(rawProgramStageInstance.createdAtClient, DATE_TIME_FORMAT),
      updatedAtClient: dayjs(rawProgramStageInstance.updatedAtClient, DATE_TIME_FORMAT),
      lastSynchronized: dayjs(rawProgramStageInstance.lastSynchronized, DATE_TIME_FORMAT),
      dueDate: dayjs(rawProgramStageInstance.dueDate, DATE_TIME_FORMAT),
      executionDate: dayjs(rawProgramStageInstance.executionDate, DATE_TIME_FORMAT),
      completedDate: dayjs(rawProgramStageInstance.completedDate, DATE_TIME_FORMAT),
      deletedAt: dayjs(rawProgramStageInstance.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertProgramStageInstanceToProgramStageInstanceRawValue(
    programStageInstance: IProgramStageInstance | (Partial<NewProgramStageInstance> & ProgramStageInstanceFormDefaults)
  ): ProgramStageInstanceFormRawValue | PartialWithRequiredKeyOf<NewProgramStageInstanceFormRawValue> {
    return {
      ...programStageInstance,
      created: programStageInstance.created ? programStageInstance.created.format(DATE_TIME_FORMAT) : undefined,
      updated: programStageInstance.updated ? programStageInstance.updated.format(DATE_TIME_FORMAT) : undefined,
      createdAtClient: programStageInstance.createdAtClient ? programStageInstance.createdAtClient.format(DATE_TIME_FORMAT) : undefined,
      updatedAtClient: programStageInstance.updatedAtClient ? programStageInstance.updatedAtClient.format(DATE_TIME_FORMAT) : undefined,
      lastSynchronized: programStageInstance.lastSynchronized ? programStageInstance.lastSynchronized.format(DATE_TIME_FORMAT) : undefined,
      dueDate: programStageInstance.dueDate ? programStageInstance.dueDate.format(DATE_TIME_FORMAT) : undefined,
      executionDate: programStageInstance.executionDate ? programStageInstance.executionDate.format(DATE_TIME_FORMAT) : undefined,
      completedDate: programStageInstance.completedDate ? programStageInstance.completedDate.format(DATE_TIME_FORMAT) : undefined,
      deletedAt: programStageInstance.deletedAt ? programStageInstance.deletedAt.format(DATE_TIME_FORMAT) : undefined,
      comments: programStageInstance.comments ?? [],
    };
  }
}
