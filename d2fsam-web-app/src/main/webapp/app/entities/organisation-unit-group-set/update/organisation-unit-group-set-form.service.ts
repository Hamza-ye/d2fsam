import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrganisationUnitGroupSet, NewOrganisationUnitGroupSet } from '../organisation-unit-group-set.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrganisationUnitGroupSet for edit and NewOrganisationUnitGroupSetFormGroupInput for create.
 */
type OrganisationUnitGroupSetFormGroupInput = IOrganisationUnitGroupSet | PartialWithRequiredKeyOf<NewOrganisationUnitGroupSet>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrganisationUnitGroupSet | NewOrganisationUnitGroupSet> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type OrganisationUnitGroupSetFormRawValue = FormValueOf<IOrganisationUnitGroupSet>;

type NewOrganisationUnitGroupSetFormRawValue = FormValueOf<NewOrganisationUnitGroupSet>;

type OrganisationUnitGroupSetFormDefaults = Pick<
  NewOrganisationUnitGroupSet,
  'id' | 'created' | 'updated' | 'compulsory' | 'includeSubhierarchyInAnalytics' | 'inactive' | 'organisationUnitGroups'
>;

type OrganisationUnitGroupSetFormGroupContent = {
  id: FormControl<OrganisationUnitGroupSetFormRawValue['id'] | NewOrganisationUnitGroupSet['id']>;
  uid: FormControl<OrganisationUnitGroupSetFormRawValue['uid']>;
  code: FormControl<OrganisationUnitGroupSetFormRawValue['code']>;
  name: FormControl<OrganisationUnitGroupSetFormRawValue['name']>;
  created: FormControl<OrganisationUnitGroupSetFormRawValue['created']>;
  updated: FormControl<OrganisationUnitGroupSetFormRawValue['updated']>;
  compulsory: FormControl<OrganisationUnitGroupSetFormRawValue['compulsory']>;
  includeSubhierarchyInAnalytics: FormControl<OrganisationUnitGroupSetFormRawValue['includeSubhierarchyInAnalytics']>;
  inactive: FormControl<OrganisationUnitGroupSetFormRawValue['inactive']>;
  createdBy: FormControl<OrganisationUnitGroupSetFormRawValue['createdBy']>;
  updatedBy: FormControl<OrganisationUnitGroupSetFormRawValue['updatedBy']>;
  organisationUnitGroups: FormControl<OrganisationUnitGroupSetFormRawValue['organisationUnitGroups']>;
};

export type OrganisationUnitGroupSetFormGroup = FormGroup<OrganisationUnitGroupSetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitGroupSetFormService {
  createOrganisationUnitGroupSetFormGroup(
    organisationUnitGroupSet: OrganisationUnitGroupSetFormGroupInput = { id: null }
  ): OrganisationUnitGroupSetFormGroup {
    const organisationUnitGroupSetRawValue = this.convertOrganisationUnitGroupSetToOrganisationUnitGroupSetRawValue({
      ...this.getFormDefaults(),
      ...organisationUnitGroupSet,
    });
    return new FormGroup<OrganisationUnitGroupSetFormGroupContent>({
      id: new FormControl(
        { value: organisationUnitGroupSetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(organisationUnitGroupSetRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(organisationUnitGroupSetRawValue.code),
      name: new FormControl(organisationUnitGroupSetRawValue.name, {
        validators: [Validators.required],
      }),
      created: new FormControl(organisationUnitGroupSetRawValue.created),
      updated: new FormControl(organisationUnitGroupSetRawValue.updated),
      compulsory: new FormControl(organisationUnitGroupSetRawValue.compulsory),
      includeSubhierarchyInAnalytics: new FormControl(organisationUnitGroupSetRawValue.includeSubhierarchyInAnalytics),
      inactive: new FormControl(organisationUnitGroupSetRawValue.inactive),
      createdBy: new FormControl(organisationUnitGroupSetRawValue.createdBy),
      updatedBy: new FormControl(organisationUnitGroupSetRawValue.updatedBy),
      organisationUnitGroups: new FormControl(organisationUnitGroupSetRawValue.organisationUnitGroups ?? []),
    });
  }

  getOrganisationUnitGroupSet(form: OrganisationUnitGroupSetFormGroup): IOrganisationUnitGroupSet | NewOrganisationUnitGroupSet {
    return this.convertOrganisationUnitGroupSetRawValueToOrganisationUnitGroupSet(
      form.getRawValue() as OrganisationUnitGroupSetFormRawValue | NewOrganisationUnitGroupSetFormRawValue
    );
  }

  resetForm(form: OrganisationUnitGroupSetFormGroup, organisationUnitGroupSet: OrganisationUnitGroupSetFormGroupInput): void {
    const organisationUnitGroupSetRawValue = this.convertOrganisationUnitGroupSetToOrganisationUnitGroupSetRawValue({
      ...this.getFormDefaults(),
      ...organisationUnitGroupSet,
    });
    form.reset(
      {
        ...organisationUnitGroupSetRawValue,
        id: { value: organisationUnitGroupSetRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrganisationUnitGroupSetFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      compulsory: false,
      includeSubhierarchyInAnalytics: false,
      inactive: false,
      organisationUnitGroups: [],
    };
  }

  private convertOrganisationUnitGroupSetRawValueToOrganisationUnitGroupSet(
    rawOrganisationUnitGroupSet: OrganisationUnitGroupSetFormRawValue | NewOrganisationUnitGroupSetFormRawValue
  ): IOrganisationUnitGroupSet | NewOrganisationUnitGroupSet {
    return {
      ...rawOrganisationUnitGroupSet,
      created: dayjs(rawOrganisationUnitGroupSet.created, DATE_TIME_FORMAT),
      updated: dayjs(rawOrganisationUnitGroupSet.updated, DATE_TIME_FORMAT),
    };
  }

  private convertOrganisationUnitGroupSetToOrganisationUnitGroupSetRawValue(
    organisationUnitGroupSet: IOrganisationUnitGroupSet | (Partial<NewOrganisationUnitGroupSet> & OrganisationUnitGroupSetFormDefaults)
  ): OrganisationUnitGroupSetFormRawValue | PartialWithRequiredKeyOf<NewOrganisationUnitGroupSetFormRawValue> {
    return {
      ...organisationUnitGroupSet,
      created: organisationUnitGroupSet.created ? organisationUnitGroupSet.created.format(DATE_TIME_FORMAT) : undefined,
      updated: organisationUnitGroupSet.updated ? organisationUnitGroupSet.updated.format(DATE_TIME_FORMAT) : undefined,
      organisationUnitGroups: organisationUnitGroupSet.organisationUnitGroups ?? [],
    };
  }
}
