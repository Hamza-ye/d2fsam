import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUserData, NewUserData } from '../user-data.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserData for edit and NewUserDataFormGroupInput for create.
 */
type UserDataFormGroupInput = IUserData | PartialWithRequiredKeyOf<NewUserData>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUserData | NewUserData> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type UserDataFormRawValue = FormValueOf<IUserData>;

type NewUserDataFormRawValue = FormValueOf<NewUserData>;

type UserDataFormDefaults = Pick<
  NewUserData,
  | 'id'
  | 'created'
  | 'updated'
  | 'inactive'
  | 'organisationUnits'
  | 'teiSearchOrganisationUnits'
  | 'dataViewOrganisationUnits'
  | 'userAuthorityGroups'
  | 'groups'
  | 'teams'
>;

type UserDataFormGroupContent = {
  id: FormControl<UserDataFormRawValue['id'] | NewUserData['id']>;
  uid: FormControl<UserDataFormRawValue['uid']>;
  code: FormControl<UserDataFormRawValue['code']>;
  name: FormControl<UserDataFormRawValue['name']>;
  created: FormControl<UserDataFormRawValue['created']>;
  updated: FormControl<UserDataFormRawValue['updated']>;
  uuid: FormControl<UserDataFormRawValue['uuid']>;
  inactive: FormControl<UserDataFormRawValue['inactive']>;
  createdBy: FormControl<UserDataFormRawValue['createdBy']>;
  updatedBy: FormControl<UserDataFormRawValue['updatedBy']>;
  organisationUnits: FormControl<UserDataFormRawValue['organisationUnits']>;
  teiSearchOrganisationUnits: FormControl<UserDataFormRawValue['teiSearchOrganisationUnits']>;
  dataViewOrganisationUnits: FormControl<UserDataFormRawValue['dataViewOrganisationUnits']>;
  userAuthorityGroups: FormControl<UserDataFormRawValue['userAuthorityGroups']>;
  groups: FormControl<UserDataFormRawValue['groups']>;
  teams: FormControl<UserDataFormRawValue['teams']>;
};

export type UserDataFormGroup = FormGroup<UserDataFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserDataFormService {
  createUserDataFormGroup(userData: UserDataFormGroupInput = { id: null }): UserDataFormGroup {
    const userDataRawValue = this.convertUserDataToUserDataRawValue({
      ...this.getFormDefaults(),
      ...userData,
    });
    return new FormGroup<UserDataFormGroupContent>({
      id: new FormControl(
        { value: userDataRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(userDataRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(userDataRawValue.code),
      name: new FormControl(userDataRawValue.name),
      created: new FormControl(userDataRawValue.created),
      updated: new FormControl(userDataRawValue.updated),
      uuid: new FormControl(userDataRawValue.uuid),
      inactive: new FormControl(userDataRawValue.inactive),
      createdBy: new FormControl(userDataRawValue.createdBy),
      updatedBy: new FormControl(userDataRawValue.updatedBy),
      organisationUnits: new FormControl(userDataRawValue.organisationUnits ?? []),
      teiSearchOrganisationUnits: new FormControl(userDataRawValue.teiSearchOrganisationUnits ?? []),
      dataViewOrganisationUnits: new FormControl(userDataRawValue.dataViewOrganisationUnits ?? []),
      userAuthorityGroups: new FormControl(userDataRawValue.userAuthorityGroups ?? []),
      groups: new FormControl(userDataRawValue.groups ?? []),
      teams: new FormControl(userDataRawValue.teams ?? []),
    });
  }

  getUserData(form: UserDataFormGroup): IUserData | NewUserData {
    return this.convertUserDataRawValueToUserData(form.getRawValue() as UserDataFormRawValue | NewUserDataFormRawValue);
  }

  resetForm(form: UserDataFormGroup, userData: UserDataFormGroupInput): void {
    const userDataRawValue = this.convertUserDataToUserDataRawValue({ ...this.getFormDefaults(), ...userData });
    form.reset(
      {
        ...userDataRawValue,
        id: { value: userDataRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserDataFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      organisationUnits: [],
      teiSearchOrganisationUnits: [],
      dataViewOrganisationUnits: [],
      userAuthorityGroups: [],
      groups: [],
      teams: [],
    };
  }

  private convertUserDataRawValueToUserData(rawUserData: UserDataFormRawValue | NewUserDataFormRawValue): IUserData | NewUserData {
    return {
      ...rawUserData,
      created: dayjs(rawUserData.created, DATE_TIME_FORMAT),
      updated: dayjs(rawUserData.updated, DATE_TIME_FORMAT),
    };
  }

  private convertUserDataToUserDataRawValue(
    userData: IUserData | (Partial<NewUserData> & UserDataFormDefaults)
  ): UserDataFormRawValue | PartialWithRequiredKeyOf<NewUserDataFormRawValue> {
    return {
      ...userData,
      created: userData.created ? userData.created.format(DATE_TIME_FORMAT) : undefined,
      updated: userData.updated ? userData.updated.format(DATE_TIME_FORMAT) : undefined,
      organisationUnits: userData.organisationUnits ?? [],
      teiSearchOrganisationUnits: userData.teiSearchOrganisationUnits ?? [],
      dataViewOrganisationUnits: userData.dataViewOrganisationUnits ?? [],
      userAuthorityGroups: userData.userAuthorityGroups ?? [],
      groups: userData.groups ?? [],
      teams: userData.teams ?? [],
    };
  }
}
