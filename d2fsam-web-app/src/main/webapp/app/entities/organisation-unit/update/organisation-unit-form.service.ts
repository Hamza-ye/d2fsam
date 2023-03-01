import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrganisationUnit, NewOrganisationUnit } from '../organisation-unit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrganisationUnit for edit and NewOrganisationUnitFormGroupInput for create.
 */
type OrganisationUnitFormGroupInput = IOrganisationUnit | PartialWithRequiredKeyOf<NewOrganisationUnit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrganisationUnit | NewOrganisationUnit> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type OrganisationUnitFormRawValue = FormValueOf<IOrganisationUnit>;

type NewOrganisationUnitFormRawValue = FormValueOf<NewOrganisationUnit>;

type OrganisationUnitFormDefaults = Pick<
  NewOrganisationUnit,
  'id' | 'created' | 'updated' | 'inactive' | 'programs' | 'targetedInActivities' | 'groups' | 'users' | 'searchingUsers' | 'dataViewUsers'
>;

type OrganisationUnitFormGroupContent = {
  id: FormControl<OrganisationUnitFormRawValue['id'] | NewOrganisationUnit['id']>;
  uid: FormControl<OrganisationUnitFormRawValue['uid']>;
  code: FormControl<OrganisationUnitFormRawValue['code']>;
  name: FormControl<OrganisationUnitFormRawValue['name']>;
  shortName: FormControl<OrganisationUnitFormRawValue['shortName']>;
  created: FormControl<OrganisationUnitFormRawValue['created']>;
  updated: FormControl<OrganisationUnitFormRawValue['updated']>;
  path: FormControl<OrganisationUnitFormRawValue['path']>;
  hierarchyLevel: FormControl<OrganisationUnitFormRawValue['hierarchyLevel']>;
  openingDate: FormControl<OrganisationUnitFormRawValue['openingDate']>;
  comment: FormControl<OrganisationUnitFormRawValue['comment']>;
  closedDate: FormControl<OrganisationUnitFormRawValue['closedDate']>;
  url: FormControl<OrganisationUnitFormRawValue['url']>;
  contactPerson: FormControl<OrganisationUnitFormRawValue['contactPerson']>;
  address: FormControl<OrganisationUnitFormRawValue['address']>;
  email: FormControl<OrganisationUnitFormRawValue['email']>;
  phoneNumber: FormControl<OrganisationUnitFormRawValue['phoneNumber']>;
  organisationUnitType: FormControl<OrganisationUnitFormRawValue['organisationUnitType']>;
  inactive: FormControl<OrganisationUnitFormRawValue['inactive']>;
  parent: FormControl<OrganisationUnitFormRawValue['parent']>;
  hfHomeSubVillage: FormControl<OrganisationUnitFormRawValue['hfHomeSubVillage']>;
  servicingHf: FormControl<OrganisationUnitFormRawValue['servicingHf']>;
  image: FormControl<OrganisationUnitFormRawValue['image']>;
  createdBy: FormControl<OrganisationUnitFormRawValue['createdBy']>;
  updatedBy: FormControl<OrganisationUnitFormRawValue['updatedBy']>;
  assignedChv: FormControl<OrganisationUnitFormRawValue['assignedChv']>;
  programs: FormControl<OrganisationUnitFormRawValue['programs']>;
  targetedInActivities: FormControl<OrganisationUnitFormRawValue['targetedInActivities']>;
  groups: FormControl<OrganisationUnitFormRawValue['groups']>;
  users: FormControl<OrganisationUnitFormRawValue['users']>;
  searchingUsers: FormControl<OrganisationUnitFormRawValue['searchingUsers']>;
  dataViewUsers: FormControl<OrganisationUnitFormRawValue['dataViewUsers']>;
};

export type OrganisationUnitFormGroup = FormGroup<OrganisationUnitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitFormService {
  createOrganisationUnitFormGroup(organisationUnit: OrganisationUnitFormGroupInput = { id: null }): OrganisationUnitFormGroup {
    const organisationUnitRawValue = this.convertOrganisationUnitToOrganisationUnitRawValue({
      ...this.getFormDefaults(),
      ...organisationUnit,
    });
    return new FormGroup<OrganisationUnitFormGroupContent>({
      id: new FormControl(
        { value: organisationUnitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(organisationUnitRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(organisationUnitRawValue.code),
      name: new FormControl(organisationUnitRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(organisationUnitRawValue.shortName, {
        validators: [Validators.maxLength(50)],
      }),
      created: new FormControl(organisationUnitRawValue.created),
      updated: new FormControl(organisationUnitRawValue.updated),
      path: new FormControl(organisationUnitRawValue.path),
      hierarchyLevel: new FormControl(organisationUnitRawValue.hierarchyLevel),
      openingDate: new FormControl(organisationUnitRawValue.openingDate),
      comment: new FormControl(organisationUnitRawValue.comment),
      closedDate: new FormControl(organisationUnitRawValue.closedDate),
      url: new FormControl(organisationUnitRawValue.url),
      contactPerson: new FormControl(organisationUnitRawValue.contactPerson),
      address: new FormControl(organisationUnitRawValue.address),
      email: new FormControl(organisationUnitRawValue.email),
      phoneNumber: new FormControl(organisationUnitRawValue.phoneNumber),
      organisationUnitType: new FormControl(organisationUnitRawValue.organisationUnitType, {
        validators: [Validators.required],
      }),
      inactive: new FormControl(organisationUnitRawValue.inactive),
      parent: new FormControl(organisationUnitRawValue.parent),
      hfHomeSubVillage: new FormControl(organisationUnitRawValue.hfHomeSubVillage),
      servicingHf: new FormControl(organisationUnitRawValue.servicingHf),
      image: new FormControl(organisationUnitRawValue.image),
      createdBy: new FormControl(organisationUnitRawValue.createdBy),
      updatedBy: new FormControl(organisationUnitRawValue.updatedBy),
      assignedChv: new FormControl(organisationUnitRawValue.assignedChv),
      programs: new FormControl(organisationUnitRawValue.programs ?? []),
      targetedInActivities: new FormControl(organisationUnitRawValue.targetedInActivities ?? []),
      groups: new FormControl(organisationUnitRawValue.groups ?? []),
      users: new FormControl(organisationUnitRawValue.users ?? []),
      searchingUsers: new FormControl(organisationUnitRawValue.searchingUsers ?? []),
      dataViewUsers: new FormControl(organisationUnitRawValue.dataViewUsers ?? []),
    });
  }

  getOrganisationUnit(form: OrganisationUnitFormGroup): IOrganisationUnit | NewOrganisationUnit {
    return this.convertOrganisationUnitRawValueToOrganisationUnit(
      form.getRawValue() as OrganisationUnitFormRawValue | NewOrganisationUnitFormRawValue
    );
  }

  resetForm(form: OrganisationUnitFormGroup, organisationUnit: OrganisationUnitFormGroupInput): void {
    const organisationUnitRawValue = this.convertOrganisationUnitToOrganisationUnitRawValue({
      ...this.getFormDefaults(),
      ...organisationUnit,
    });
    form.reset(
      {
        ...organisationUnitRawValue,
        id: { value: organisationUnitRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrganisationUnitFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      programs: [],
      targetedInActivities: [],
      groups: [],
      users: [],
      searchingUsers: [],
      dataViewUsers: [],
    };
  }

  private convertOrganisationUnitRawValueToOrganisationUnit(
    rawOrganisationUnit: OrganisationUnitFormRawValue | NewOrganisationUnitFormRawValue
  ): IOrganisationUnit | NewOrganisationUnit {
    return {
      ...rawOrganisationUnit,
      created: dayjs(rawOrganisationUnit.created, DATE_TIME_FORMAT),
      updated: dayjs(rawOrganisationUnit.updated, DATE_TIME_FORMAT),
    };
  }

  private convertOrganisationUnitToOrganisationUnitRawValue(
    organisationUnit: IOrganisationUnit | (Partial<NewOrganisationUnit> & OrganisationUnitFormDefaults)
  ): OrganisationUnitFormRawValue | PartialWithRequiredKeyOf<NewOrganisationUnitFormRawValue> {
    return {
      ...organisationUnit,
      created: organisationUnit.created ? organisationUnit.created.format(DATE_TIME_FORMAT) : undefined,
      updated: organisationUnit.updated ? organisationUnit.updated.format(DATE_TIME_FORMAT) : undefined,
      programs: organisationUnit.programs ?? [],
      targetedInActivities: organisationUnit.targetedInActivities ?? [],
      groups: organisationUnit.groups ?? [],
      users: organisationUnit.users ?? [],
      searchingUsers: organisationUnit.searchingUsers ?? [],
      dataViewUsers: organisationUnit.dataViewUsers ?? [],
    };
  }
}
