import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUserGroup, NewUserGroup } from '../user-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserGroup for edit and NewUserGroupFormGroupInput for create.
 */
type UserGroupFormGroupInput = IUserGroup | PartialWithRequiredKeyOf<NewUserGroup>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUserGroup | NewUserGroup> = Omit<T, 'created' | 'updated' | 'activeFrom' | 'activeTo'> & {
  created?: string | null;
  updated?: string | null;
  activeFrom?: string | null;
  activeTo?: string | null;
};

type UserGroupFormRawValue = FormValueOf<IUserGroup>;

type NewUserGroupFormRawValue = FormValueOf<NewUserGroup>;

type UserGroupFormDefaults = Pick<
  NewUserGroup,
  'id' | 'created' | 'updated' | 'activeFrom' | 'activeTo' | 'inactive' | 'members' | 'managedGroups' | 'managedByGroups'
>;

type UserGroupFormGroupContent = {
  id: FormControl<UserGroupFormRawValue['id'] | NewUserGroup['id']>;
  uid: FormControl<UserGroupFormRawValue['uid']>;
  code: FormControl<UserGroupFormRawValue['code']>;
  name: FormControl<UserGroupFormRawValue['name']>;
  uuid: FormControl<UserGroupFormRawValue['uuid']>;
  created: FormControl<UserGroupFormRawValue['created']>;
  updated: FormControl<UserGroupFormRawValue['updated']>;
  activeFrom: FormControl<UserGroupFormRawValue['activeFrom']>;
  activeTo: FormControl<UserGroupFormRawValue['activeTo']>;
  inactive: FormControl<UserGroupFormRawValue['inactive']>;
  createdBy: FormControl<UserGroupFormRawValue['createdBy']>;
  updatedBy: FormControl<UserGroupFormRawValue['updatedBy']>;
  members: FormControl<UserGroupFormRawValue['members']>;
  managedGroups: FormControl<UserGroupFormRawValue['managedGroups']>;
  managedByGroups: FormControl<UserGroupFormRawValue['managedByGroups']>;
};

export type UserGroupFormGroup = FormGroup<UserGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserGroupFormService {
  createUserGroupFormGroup(userGroup: UserGroupFormGroupInput = { id: null }): UserGroupFormGroup {
    const userGroupRawValue = this.convertUserGroupToUserGroupRawValue({
      ...this.getFormDefaults(),
      ...userGroup,
    });
    return new FormGroup<UserGroupFormGroupContent>({
      id: new FormControl(
        { value: userGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(userGroupRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(userGroupRawValue.code),
      name: new FormControl(userGroupRawValue.name, {
        validators: [Validators.required],
      }),
      uuid: new FormControl(userGroupRawValue.uuid),
      created: new FormControl(userGroupRawValue.created),
      updated: new FormControl(userGroupRawValue.updated),
      activeFrom: new FormControl(userGroupRawValue.activeFrom),
      activeTo: new FormControl(userGroupRawValue.activeTo),
      inactive: new FormControl(userGroupRawValue.inactive),
      createdBy: new FormControl(userGroupRawValue.createdBy),
      updatedBy: new FormControl(userGroupRawValue.updatedBy),
      members: new FormControl(userGroupRawValue.members ?? []),
      managedGroups: new FormControl(userGroupRawValue.managedGroups ?? []),
      managedByGroups: new FormControl(userGroupRawValue.managedByGroups ?? []),
    });
  }

  getUserGroup(form: UserGroupFormGroup): IUserGroup | NewUserGroup {
    return this.convertUserGroupRawValueToUserGroup(form.getRawValue() as UserGroupFormRawValue | NewUserGroupFormRawValue);
  }

  resetForm(form: UserGroupFormGroup, userGroup: UserGroupFormGroupInput): void {
    const userGroupRawValue = this.convertUserGroupToUserGroupRawValue({ ...this.getFormDefaults(), ...userGroup });
    form.reset(
      {
        ...userGroupRawValue,
        id: { value: userGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserGroupFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      activeFrom: currentTime,
      activeTo: currentTime,
      inactive: false,
      members: [],
      managedGroups: [],
      managedByGroups: [],
    };
  }

  private convertUserGroupRawValueToUserGroup(rawUserGroup: UserGroupFormRawValue | NewUserGroupFormRawValue): IUserGroup | NewUserGroup {
    return {
      ...rawUserGroup,
      created: dayjs(rawUserGroup.created, DATE_TIME_FORMAT),
      updated: dayjs(rawUserGroup.updated, DATE_TIME_FORMAT),
      activeFrom: dayjs(rawUserGroup.activeFrom, DATE_TIME_FORMAT),
      activeTo: dayjs(rawUserGroup.activeTo, DATE_TIME_FORMAT),
    };
  }

  private convertUserGroupToUserGroupRawValue(
    userGroup: IUserGroup | (Partial<NewUserGroup> & UserGroupFormDefaults)
  ): UserGroupFormRawValue | PartialWithRequiredKeyOf<NewUserGroupFormRawValue> {
    return {
      ...userGroup,
      created: userGroup.created ? userGroup.created.format(DATE_TIME_FORMAT) : undefined,
      updated: userGroup.updated ? userGroup.updated.format(DATE_TIME_FORMAT) : undefined,
      activeFrom: userGroup.activeFrom ? userGroup.activeFrom.format(DATE_TIME_FORMAT) : undefined,
      activeTo: userGroup.activeTo ? userGroup.activeTo.format(DATE_TIME_FORMAT) : undefined,
      members: userGroup.members ?? [],
      managedGroups: userGroup.managedGroups ?? [],
      managedByGroups: userGroup.managedByGroups ?? [],
    };
  }
}
