import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrganisationUnitGroup, NewOrganisationUnitGroup } from '../organisation-unit-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrganisationUnitGroup for edit and NewOrganisationUnitGroupFormGroupInput for create.
 */
type OrganisationUnitGroupFormGroupInput = IOrganisationUnitGroup | PartialWithRequiredKeyOf<NewOrganisationUnitGroup>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrganisationUnitGroup | NewOrganisationUnitGroup> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type OrganisationUnitGroupFormRawValue = FormValueOf<IOrganisationUnitGroup>;

type NewOrganisationUnitGroupFormRawValue = FormValueOf<NewOrganisationUnitGroup>;

type OrganisationUnitGroupFormDefaults = Pick<
  NewOrganisationUnitGroup,
  'id' | 'created' | 'updated' | 'inactive' | 'members' | 'groupSets'
>;

type OrganisationUnitGroupFormGroupContent = {
  id: FormControl<OrganisationUnitGroupFormRawValue['id'] | NewOrganisationUnitGroup['id']>;
  uid: FormControl<OrganisationUnitGroupFormRawValue['uid']>;
  code: FormControl<OrganisationUnitGroupFormRawValue['code']>;
  name: FormControl<OrganisationUnitGroupFormRawValue['name']>;
  shortName: FormControl<OrganisationUnitGroupFormRawValue['shortName']>;
  created: FormControl<OrganisationUnitGroupFormRawValue['created']>;
  updated: FormControl<OrganisationUnitGroupFormRawValue['updated']>;
  symbol: FormControl<OrganisationUnitGroupFormRawValue['symbol']>;
  color: FormControl<OrganisationUnitGroupFormRawValue['color']>;
  inactive: FormControl<OrganisationUnitGroupFormRawValue['inactive']>;
  createdBy: FormControl<OrganisationUnitGroupFormRawValue['createdBy']>;
  updatedBy: FormControl<OrganisationUnitGroupFormRawValue['updatedBy']>;
  members: FormControl<OrganisationUnitGroupFormRawValue['members']>;
  groupSets: FormControl<OrganisationUnitGroupFormRawValue['groupSets']>;
};

export type OrganisationUnitGroupFormGroup = FormGroup<OrganisationUnitGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitGroupFormService {
  createOrganisationUnitGroupFormGroup(
    organisationUnitGroup: OrganisationUnitGroupFormGroupInput = { id: null }
  ): OrganisationUnitGroupFormGroup {
    const organisationUnitGroupRawValue = this.convertOrganisationUnitGroupToOrganisationUnitGroupRawValue({
      ...this.getFormDefaults(),
      ...organisationUnitGroup,
    });
    return new FormGroup<OrganisationUnitGroupFormGroupContent>({
      id: new FormControl(
        { value: organisationUnitGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(organisationUnitGroupRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(organisationUnitGroupRawValue.code),
      name: new FormControl(organisationUnitGroupRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(organisationUnitGroupRawValue.shortName, {
        validators: [Validators.maxLength(50)],
      }),
      created: new FormControl(organisationUnitGroupRawValue.created),
      updated: new FormControl(organisationUnitGroupRawValue.updated),
      symbol: new FormControl(organisationUnitGroupRawValue.symbol),
      color: new FormControl(organisationUnitGroupRawValue.color),
      inactive: new FormControl(organisationUnitGroupRawValue.inactive),
      createdBy: new FormControl(organisationUnitGroupRawValue.createdBy),
      updatedBy: new FormControl(organisationUnitGroupRawValue.updatedBy),
      members: new FormControl(organisationUnitGroupRawValue.members ?? []),
      groupSets: new FormControl(organisationUnitGroupRawValue.groupSets ?? []),
    });
  }

  getOrganisationUnitGroup(form: OrganisationUnitGroupFormGroup): IOrganisationUnitGroup | NewOrganisationUnitGroup {
    return this.convertOrganisationUnitGroupRawValueToOrganisationUnitGroup(
      form.getRawValue() as OrganisationUnitGroupFormRawValue | NewOrganisationUnitGroupFormRawValue
    );
  }

  resetForm(form: OrganisationUnitGroupFormGroup, organisationUnitGroup: OrganisationUnitGroupFormGroupInput): void {
    const organisationUnitGroupRawValue = this.convertOrganisationUnitGroupToOrganisationUnitGroupRawValue({
      ...this.getFormDefaults(),
      ...organisationUnitGroup,
    });
    form.reset(
      {
        ...organisationUnitGroupRawValue,
        id: { value: organisationUnitGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrganisationUnitGroupFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      members: [],
      groupSets: [],
    };
  }

  private convertOrganisationUnitGroupRawValueToOrganisationUnitGroup(
    rawOrganisationUnitGroup: OrganisationUnitGroupFormRawValue | NewOrganisationUnitGroupFormRawValue
  ): IOrganisationUnitGroup | NewOrganisationUnitGroup {
    return {
      ...rawOrganisationUnitGroup,
      created: dayjs(rawOrganisationUnitGroup.created, DATE_TIME_FORMAT),
      updated: dayjs(rawOrganisationUnitGroup.updated, DATE_TIME_FORMAT),
    };
  }

  private convertOrganisationUnitGroupToOrganisationUnitGroupRawValue(
    organisationUnitGroup: IOrganisationUnitGroup | (Partial<NewOrganisationUnitGroup> & OrganisationUnitGroupFormDefaults)
  ): OrganisationUnitGroupFormRawValue | PartialWithRequiredKeyOf<NewOrganisationUnitGroupFormRawValue> {
    return {
      ...organisationUnitGroup,
      created: organisationUnitGroup.created ? organisationUnitGroup.created.format(DATE_TIME_FORMAT) : undefined,
      updated: organisationUnitGroup.updated ? organisationUnitGroup.updated.format(DATE_TIME_FORMAT) : undefined,
      members: organisationUnitGroup.members ?? [],
      groupSets: organisationUnitGroup.groupSets ?? [],
    };
  }
}
