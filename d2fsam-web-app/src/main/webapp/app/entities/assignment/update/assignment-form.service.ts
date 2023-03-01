import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAssignment, NewAssignment } from '../assignment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAssignment for edit and NewAssignmentFormGroupInput for create.
 */
type AssignmentFormGroupInput = IAssignment | PartialWithRequiredKeyOf<NewAssignment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAssignment | NewAssignment> = Omit<T, 'created' | 'updated' | 'deletedAt'> & {
  created?: string | null;
  updated?: string | null;
  deletedAt?: string | null;
};

type AssignmentFormRawValue = FormValueOf<IAssignment>;

type NewAssignmentFormRawValue = FormValueOf<NewAssignment>;

type AssignmentFormDefaults = Pick<NewAssignment, 'id' | 'created' | 'updated' | 'deleted' | 'deletedAt'>;

type AssignmentFormGroupContent = {
  id: FormControl<AssignmentFormRawValue['id'] | NewAssignment['id']>;
  uid: FormControl<AssignmentFormRawValue['uid']>;
  code: FormControl<AssignmentFormRawValue['code']>;
  created: FormControl<AssignmentFormRawValue['created']>;
  updated: FormControl<AssignmentFormRawValue['updated']>;
  description: FormControl<AssignmentFormRawValue['description']>;
  startDate: FormControl<AssignmentFormRawValue['startDate']>;
  startPeriod: FormControl<AssignmentFormRawValue['startPeriod']>;
  targetSource: FormControl<AssignmentFormRawValue['targetSource']>;
  status: FormControl<AssignmentFormRawValue['status']>;
  deleted: FormControl<AssignmentFormRawValue['deleted']>;
  deletedAt: FormControl<AssignmentFormRawValue['deletedAt']>;
  activity: FormControl<AssignmentFormRawValue['activity']>;
  organisationUnit: FormControl<AssignmentFormRawValue['organisationUnit']>;
  assignedTeam: FormControl<AssignmentFormRawValue['assignedTeam']>;
  period: FormControl<AssignmentFormRawValue['period']>;
  createdBy: FormControl<AssignmentFormRawValue['createdBy']>;
  updatedBy: FormControl<AssignmentFormRawValue['updatedBy']>;
};

export type AssignmentFormGroup = FormGroup<AssignmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AssignmentFormService {
  createAssignmentFormGroup(assignment: AssignmentFormGroupInput = { id: null }): AssignmentFormGroup {
    const assignmentRawValue = this.convertAssignmentToAssignmentRawValue({
      ...this.getFormDefaults(),
      ...assignment,
    });
    return new FormGroup<AssignmentFormGroupContent>({
      id: new FormControl(
        { value: assignmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(assignmentRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(assignmentRawValue.code),
      created: new FormControl(assignmentRawValue.created),
      updated: new FormControl(assignmentRawValue.updated),
      description: new FormControl(assignmentRawValue.description),
      startDate: new FormControl(assignmentRawValue.startDate),
      startPeriod: new FormControl(assignmentRawValue.startPeriod),
      targetSource: new FormControl(assignmentRawValue.targetSource),
      status: new FormControl(assignmentRawValue.status),
      deleted: new FormControl(assignmentRawValue.deleted),
      deletedAt: new FormControl(assignmentRawValue.deletedAt),
      activity: new FormControl(assignmentRawValue.activity, {
        validators: [Validators.required],
      }),
      organisationUnit: new FormControl(assignmentRawValue.organisationUnit, {
        validators: [Validators.required],
      }),
      assignedTeam: new FormControl(assignmentRawValue.assignedTeam, {
        validators: [Validators.required],
      }),
      period: new FormControl(assignmentRawValue.period),
      createdBy: new FormControl(assignmentRawValue.createdBy),
      updatedBy: new FormControl(assignmentRawValue.updatedBy),
    });
  }

  getAssignment(form: AssignmentFormGroup): IAssignment | NewAssignment {
    return this.convertAssignmentRawValueToAssignment(form.getRawValue() as AssignmentFormRawValue | NewAssignmentFormRawValue);
  }

  resetForm(form: AssignmentFormGroup, assignment: AssignmentFormGroupInput): void {
    const assignmentRawValue = this.convertAssignmentToAssignmentRawValue({ ...this.getFormDefaults(), ...assignment });
    form.reset(
      {
        ...assignmentRawValue,
        id: { value: assignmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AssignmentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      deleted: false,
      deletedAt: currentTime,
    };
  }

  private convertAssignmentRawValueToAssignment(
    rawAssignment: AssignmentFormRawValue | NewAssignmentFormRawValue
  ): IAssignment | NewAssignment {
    return {
      ...rawAssignment,
      created: dayjs(rawAssignment.created, DATE_TIME_FORMAT),
      updated: dayjs(rawAssignment.updated, DATE_TIME_FORMAT),
      deletedAt: dayjs(rawAssignment.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertAssignmentToAssignmentRawValue(
    assignment: IAssignment | (Partial<NewAssignment> & AssignmentFormDefaults)
  ): AssignmentFormRawValue | PartialWithRequiredKeyOf<NewAssignmentFormRawValue> {
    return {
      ...assignment,
      created: assignment.created ? assignment.created.format(DATE_TIME_FORMAT) : undefined,
      updated: assignment.updated ? assignment.updated.format(DATE_TIME_FORMAT) : undefined,
      deletedAt: assignment.deletedAt ? assignment.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
