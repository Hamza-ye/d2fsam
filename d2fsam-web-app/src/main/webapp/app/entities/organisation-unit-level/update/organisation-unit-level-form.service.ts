import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrganisationUnitLevel, NewOrganisationUnitLevel } from '../organisation-unit-level.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrganisationUnitLevel for edit and NewOrganisationUnitLevelFormGroupInput for create.
 */
type OrganisationUnitLevelFormGroupInput = IOrganisationUnitLevel | PartialWithRequiredKeyOf<NewOrganisationUnitLevel>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrganisationUnitLevel | NewOrganisationUnitLevel> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type OrganisationUnitLevelFormRawValue = FormValueOf<IOrganisationUnitLevel>;

type NewOrganisationUnitLevelFormRawValue = FormValueOf<NewOrganisationUnitLevel>;

type OrganisationUnitLevelFormDefaults = Pick<NewOrganisationUnitLevel, 'id' | 'created' | 'updated'>;

type OrganisationUnitLevelFormGroupContent = {
  id: FormControl<OrganisationUnitLevelFormRawValue['id'] | NewOrganisationUnitLevel['id']>;
  uid: FormControl<OrganisationUnitLevelFormRawValue['uid']>;
  code: FormControl<OrganisationUnitLevelFormRawValue['code']>;
  name: FormControl<OrganisationUnitLevelFormRawValue['name']>;
  created: FormControl<OrganisationUnitLevelFormRawValue['created']>;
  updated: FormControl<OrganisationUnitLevelFormRawValue['updated']>;
  level: FormControl<OrganisationUnitLevelFormRawValue['level']>;
  offlineLevels: FormControl<OrganisationUnitLevelFormRawValue['offlineLevels']>;
};

export type OrganisationUnitLevelFormGroup = FormGroup<OrganisationUnitLevelFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitLevelFormService {
  createOrganisationUnitLevelFormGroup(
    organisationUnitLevel: OrganisationUnitLevelFormGroupInput = { id: null }
  ): OrganisationUnitLevelFormGroup {
    const organisationUnitLevelRawValue = this.convertOrganisationUnitLevelToOrganisationUnitLevelRawValue({
      ...this.getFormDefaults(),
      ...organisationUnitLevel,
    });
    return new FormGroup<OrganisationUnitLevelFormGroupContent>({
      id: new FormControl(
        { value: organisationUnitLevelRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(organisationUnitLevelRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(organisationUnitLevelRawValue.code),
      name: new FormControl(organisationUnitLevelRawValue.name),
      created: new FormControl(organisationUnitLevelRawValue.created),
      updated: new FormControl(organisationUnitLevelRawValue.updated),
      level: new FormControl(organisationUnitLevelRawValue.level),
      offlineLevels: new FormControl(organisationUnitLevelRawValue.offlineLevels),
    });
  }

  getOrganisationUnitLevel(form: OrganisationUnitLevelFormGroup): IOrganisationUnitLevel | NewOrganisationUnitLevel {
    return this.convertOrganisationUnitLevelRawValueToOrganisationUnitLevel(
      form.getRawValue() as OrganisationUnitLevelFormRawValue | NewOrganisationUnitLevelFormRawValue
    );
  }

  resetForm(form: OrganisationUnitLevelFormGroup, organisationUnitLevel: OrganisationUnitLevelFormGroupInput): void {
    const organisationUnitLevelRawValue = this.convertOrganisationUnitLevelToOrganisationUnitLevelRawValue({
      ...this.getFormDefaults(),
      ...organisationUnitLevel,
    });
    form.reset(
      {
        ...organisationUnitLevelRawValue,
        id: { value: organisationUnitLevelRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrganisationUnitLevelFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertOrganisationUnitLevelRawValueToOrganisationUnitLevel(
    rawOrganisationUnitLevel: OrganisationUnitLevelFormRawValue | NewOrganisationUnitLevelFormRawValue
  ): IOrganisationUnitLevel | NewOrganisationUnitLevel {
    return {
      ...rawOrganisationUnitLevel,
      created: dayjs(rawOrganisationUnitLevel.created, DATE_TIME_FORMAT),
      updated: dayjs(rawOrganisationUnitLevel.updated, DATE_TIME_FORMAT),
    };
  }

  private convertOrganisationUnitLevelToOrganisationUnitLevelRawValue(
    organisationUnitLevel: IOrganisationUnitLevel | (Partial<NewOrganisationUnitLevel> & OrganisationUnitLevelFormDefaults)
  ): OrganisationUnitLevelFormRawValue | PartialWithRequiredKeyOf<NewOrganisationUnitLevelFormRawValue> {
    return {
      ...organisationUnitLevel,
      created: organisationUnitLevel.created ? organisationUnitLevel.created.format(DATE_TIME_FORMAT) : undefined,
      updated: organisationUnitLevel.updated ? organisationUnitLevel.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
