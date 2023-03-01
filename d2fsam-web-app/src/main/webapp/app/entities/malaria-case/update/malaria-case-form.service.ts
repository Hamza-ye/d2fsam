import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMalariaCase, NewMalariaCase } from '../malaria-case.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMalariaCase for edit and NewMalariaCaseFormGroupInput for create.
 */
type MalariaCaseFormGroupInput = IMalariaCase | PartialWithRequiredKeyOf<NewMalariaCase>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMalariaCase | NewMalariaCase> = Omit<
  T,
  'entryStarted' | 'lastSynced' | 'created' | 'updated' | 'createdAtClient' | 'updatedAtClient' | 'deletedAt'
> & {
  entryStarted?: string | null;
  lastSynced?: string | null;
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  deletedAt?: string | null;
};

type MalariaCaseFormRawValue = FormValueOf<IMalariaCase>;

type NewMalariaCaseFormRawValue = FormValueOf<NewMalariaCase>;

type MalariaCaseFormDefaults = Pick<
  NewMalariaCase,
  | 'id'
  | 'entryStarted'
  | 'lastSynced'
  | 'deleted'
  | 'isPregnant'
  | 'referral'
  | 'seen'
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'deletedAt'
>;

type MalariaCaseFormGroupContent = {
  id: FormControl<MalariaCaseFormRawValue['id'] | NewMalariaCase['id']>;
  uuid: FormControl<MalariaCaseFormRawValue['uuid']>;
  code: FormControl<MalariaCaseFormRawValue['code']>;
  name: FormControl<MalariaCaseFormRawValue['name']>;
  entryStarted: FormControl<MalariaCaseFormRawValue['entryStarted']>;
  lastSynced: FormControl<MalariaCaseFormRawValue['lastSynced']>;
  deleted: FormControl<MalariaCaseFormRawValue['deleted']>;
  dateOfExamination: FormControl<MalariaCaseFormRawValue['dateOfExamination']>;
  mobile: FormControl<MalariaCaseFormRawValue['mobile']>;
  gender: FormControl<MalariaCaseFormRawValue['gender']>;
  age: FormControl<MalariaCaseFormRawValue['age']>;
  isPregnant: FormControl<MalariaCaseFormRawValue['isPregnant']>;
  malariaTestResult: FormControl<MalariaCaseFormRawValue['malariaTestResult']>;
  severity: FormControl<MalariaCaseFormRawValue['severity']>;
  referral: FormControl<MalariaCaseFormRawValue['referral']>;
  barImageUrl: FormControl<MalariaCaseFormRawValue['barImageUrl']>;
  comment: FormControl<MalariaCaseFormRawValue['comment']>;
  status: FormControl<MalariaCaseFormRawValue['status']>;
  seen: FormControl<MalariaCaseFormRawValue['seen']>;
  created: FormControl<MalariaCaseFormRawValue['created']>;
  updated: FormControl<MalariaCaseFormRawValue['updated']>;
  createdAtClient: FormControl<MalariaCaseFormRawValue['createdAtClient']>;
  updatedAtClient: FormControl<MalariaCaseFormRawValue['updatedAtClient']>;
  deletedAt: FormControl<MalariaCaseFormRawValue['deletedAt']>;
  subVillage: FormControl<MalariaCaseFormRawValue['subVillage']>;
  createdBy: FormControl<MalariaCaseFormRawValue['createdBy']>;
  updatedBy: FormControl<MalariaCaseFormRawValue['updatedBy']>;
};

export type MalariaCaseFormGroup = FormGroup<MalariaCaseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MalariaCaseFormService {
  createMalariaCaseFormGroup(malariaCase: MalariaCaseFormGroupInput = { id: null }): MalariaCaseFormGroup {
    const malariaCaseRawValue = this.convertMalariaCaseToMalariaCaseRawValue({
      ...this.getFormDefaults(),
      ...malariaCase,
    });
    return new FormGroup<MalariaCaseFormGroupContent>({
      id: new FormControl(
        { value: malariaCaseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uuid: new FormControl(malariaCaseRawValue.uuid),
      code: new FormControl(malariaCaseRawValue.code),
      name: new FormControl(malariaCaseRawValue.name),
      entryStarted: new FormControl(malariaCaseRawValue.entryStarted),
      lastSynced: new FormControl(malariaCaseRawValue.lastSynced),
      deleted: new FormControl(malariaCaseRawValue.deleted),
      dateOfExamination: new FormControl(malariaCaseRawValue.dateOfExamination),
      mobile: new FormControl(malariaCaseRawValue.mobile),
      gender: new FormControl(malariaCaseRawValue.gender),
      age: new FormControl(malariaCaseRawValue.age),
      isPregnant: new FormControl(malariaCaseRawValue.isPregnant),
      malariaTestResult: new FormControl(malariaCaseRawValue.malariaTestResult),
      severity: new FormControl(malariaCaseRawValue.severity),
      referral: new FormControl(malariaCaseRawValue.referral),
      barImageUrl: new FormControl(malariaCaseRawValue.barImageUrl),
      comment: new FormControl(malariaCaseRawValue.comment),
      status: new FormControl(malariaCaseRawValue.status),
      seen: new FormControl(malariaCaseRawValue.seen),
      created: new FormControl(malariaCaseRawValue.created),
      updated: new FormControl(malariaCaseRawValue.updated),
      createdAtClient: new FormControl(malariaCaseRawValue.createdAtClient),
      updatedAtClient: new FormControl(malariaCaseRawValue.updatedAtClient),
      deletedAt: new FormControl(malariaCaseRawValue.deletedAt),
      subVillage: new FormControl(malariaCaseRawValue.subVillage),
      createdBy: new FormControl(malariaCaseRawValue.createdBy),
      updatedBy: new FormControl(malariaCaseRawValue.updatedBy),
    });
  }

  getMalariaCase(form: MalariaCaseFormGroup): IMalariaCase | NewMalariaCase {
    return this.convertMalariaCaseRawValueToMalariaCase(form.getRawValue() as MalariaCaseFormRawValue | NewMalariaCaseFormRawValue);
  }

  resetForm(form: MalariaCaseFormGroup, malariaCase: MalariaCaseFormGroupInput): void {
    const malariaCaseRawValue = this.convertMalariaCaseToMalariaCaseRawValue({ ...this.getFormDefaults(), ...malariaCase });
    form.reset(
      {
        ...malariaCaseRawValue,
        id: { value: malariaCaseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MalariaCaseFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      entryStarted: currentTime,
      lastSynced: currentTime,
      deleted: false,
      isPregnant: false,
      referral: false,
      seen: false,
      created: currentTime,
      updated: currentTime,
      createdAtClient: currentTime,
      updatedAtClient: currentTime,
      deletedAt: currentTime,
    };
  }

  private convertMalariaCaseRawValueToMalariaCase(
    rawMalariaCase: MalariaCaseFormRawValue | NewMalariaCaseFormRawValue
  ): IMalariaCase | NewMalariaCase {
    return {
      ...rawMalariaCase,
      entryStarted: dayjs(rawMalariaCase.entryStarted, DATE_TIME_FORMAT),
      lastSynced: dayjs(rawMalariaCase.lastSynced, DATE_TIME_FORMAT),
      created: dayjs(rawMalariaCase.created, DATE_TIME_FORMAT),
      updated: dayjs(rawMalariaCase.updated, DATE_TIME_FORMAT),
      createdAtClient: dayjs(rawMalariaCase.createdAtClient, DATE_TIME_FORMAT),
      updatedAtClient: dayjs(rawMalariaCase.updatedAtClient, DATE_TIME_FORMAT),
      deletedAt: dayjs(rawMalariaCase.deletedAt, DATE_TIME_FORMAT),
    };
  }

  private convertMalariaCaseToMalariaCaseRawValue(
    malariaCase: IMalariaCase | (Partial<NewMalariaCase> & MalariaCaseFormDefaults)
  ): MalariaCaseFormRawValue | PartialWithRequiredKeyOf<NewMalariaCaseFormRawValue> {
    return {
      ...malariaCase,
      entryStarted: malariaCase.entryStarted ? malariaCase.entryStarted.format(DATE_TIME_FORMAT) : undefined,
      lastSynced: malariaCase.lastSynced ? malariaCase.lastSynced.format(DATE_TIME_FORMAT) : undefined,
      created: malariaCase.created ? malariaCase.created.format(DATE_TIME_FORMAT) : undefined,
      updated: malariaCase.updated ? malariaCase.updated.format(DATE_TIME_FORMAT) : undefined,
      createdAtClient: malariaCase.createdAtClient ? malariaCase.createdAtClient.format(DATE_TIME_FORMAT) : undefined,
      updatedAtClient: malariaCase.updatedAtClient ? malariaCase.updatedAtClient.format(DATE_TIME_FORMAT) : undefined,
      deletedAt: malariaCase.deletedAt ? malariaCase.deletedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
