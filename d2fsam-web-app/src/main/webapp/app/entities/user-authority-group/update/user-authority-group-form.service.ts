import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUserAuthorityGroup, NewUserAuthorityGroup } from '../user-authority-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserAuthorityGroup for edit and NewUserAuthorityGroupFormGroupInput for create.
 */
type UserAuthorityGroupFormGroupInput = IUserAuthorityGroup | PartialWithRequiredKeyOf<NewUserAuthorityGroup>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUserAuthorityGroup | NewUserAuthorityGroup> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type UserAuthorityGroupFormRawValue = FormValueOf<IUserAuthorityGroup>;

type NewUserAuthorityGroupFormRawValue = FormValueOf<NewUserAuthorityGroup>;

type UserAuthorityGroupFormDefaults = Pick<NewUserAuthorityGroup, 'id' | 'created' | 'updated' | 'inactive' | 'members'>;

type UserAuthorityGroupFormGroupContent = {
  id: FormControl<UserAuthorityGroupFormRawValue['id'] | NewUserAuthorityGroup['id']>;
  uid: FormControl<UserAuthorityGroupFormRawValue['uid']>;
  code: FormControl<UserAuthorityGroupFormRawValue['code']>;
  name: FormControl<UserAuthorityGroupFormRawValue['name']>;
  description: FormControl<UserAuthorityGroupFormRawValue['description']>;
  created: FormControl<UserAuthorityGroupFormRawValue['created']>;
  updated: FormControl<UserAuthorityGroupFormRawValue['updated']>;
  inactive: FormControl<UserAuthorityGroupFormRawValue['inactive']>;
  createdBy: FormControl<UserAuthorityGroupFormRawValue['createdBy']>;
  updatedBy: FormControl<UserAuthorityGroupFormRawValue['updatedBy']>;
  members: FormControl<UserAuthorityGroupFormRawValue['members']>;
};

export type UserAuthorityGroupFormGroup = FormGroup<UserAuthorityGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserAuthorityGroupFormService {
  createUserAuthorityGroupFormGroup(userAuthorityGroup: UserAuthorityGroupFormGroupInput = { id: null }): UserAuthorityGroupFormGroup {
    const userAuthorityGroupRawValue = this.convertUserAuthorityGroupToUserAuthorityGroupRawValue({
      ...this.getFormDefaults(),
      ...userAuthorityGroup,
    });
    return new FormGroup<UserAuthorityGroupFormGroupContent>({
      id: new FormControl(
        { value: userAuthorityGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(userAuthorityGroupRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(userAuthorityGroupRawValue.code),
      name: new FormControl(userAuthorityGroupRawValue.name),
      description: new FormControl(userAuthorityGroupRawValue.description),
      created: new FormControl(userAuthorityGroupRawValue.created),
      updated: new FormControl(userAuthorityGroupRawValue.updated),
      inactive: new FormControl(userAuthorityGroupRawValue.inactive),
      createdBy: new FormControl(userAuthorityGroupRawValue.createdBy),
      updatedBy: new FormControl(userAuthorityGroupRawValue.updatedBy),
      members: new FormControl(userAuthorityGroupRawValue.members ?? []),
    });
  }

  getUserAuthorityGroup(form: UserAuthorityGroupFormGroup): IUserAuthorityGroup | NewUserAuthorityGroup {
    return this.convertUserAuthorityGroupRawValueToUserAuthorityGroup(
      form.getRawValue() as UserAuthorityGroupFormRawValue | NewUserAuthorityGroupFormRawValue
    );
  }

  resetForm(form: UserAuthorityGroupFormGroup, userAuthorityGroup: UserAuthorityGroupFormGroupInput): void {
    const userAuthorityGroupRawValue = this.convertUserAuthorityGroupToUserAuthorityGroupRawValue({
      ...this.getFormDefaults(),
      ...userAuthorityGroup,
    });
    form.reset(
      {
        ...userAuthorityGroupRawValue,
        id: { value: userAuthorityGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserAuthorityGroupFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      members: [],
    };
  }

  private convertUserAuthorityGroupRawValueToUserAuthorityGroup(
    rawUserAuthorityGroup: UserAuthorityGroupFormRawValue | NewUserAuthorityGroupFormRawValue
  ): IUserAuthorityGroup | NewUserAuthorityGroup {
    return {
      ...rawUserAuthorityGroup,
      created: dayjs(rawUserAuthorityGroup.created, DATE_TIME_FORMAT),
      updated: dayjs(rawUserAuthorityGroup.updated, DATE_TIME_FORMAT),
    };
  }

  private convertUserAuthorityGroupToUserAuthorityGroupRawValue(
    userAuthorityGroup: IUserAuthorityGroup | (Partial<NewUserAuthorityGroup> & UserAuthorityGroupFormDefaults)
  ): UserAuthorityGroupFormRawValue | PartialWithRequiredKeyOf<NewUserAuthorityGroupFormRawValue> {
    return {
      ...userAuthorityGroup,
      created: userAuthorityGroup.created ? userAuthorityGroup.created.format(DATE_TIME_FORMAT) : undefined,
      updated: userAuthorityGroup.updated ? userAuthorityGroup.updated.format(DATE_TIME_FORMAT) : undefined,
      members: userAuthorityGroup.members ?? [],
    };
  }
}
