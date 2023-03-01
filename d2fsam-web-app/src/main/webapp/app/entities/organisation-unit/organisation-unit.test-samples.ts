import dayjs from 'dayjs/esm';

import { OrganisationUnitType } from 'app/entities/enumerations/organisation-unit-type.model';

import { IOrganisationUnit, NewOrganisationUnit } from './organisation-unit.model';

export const sampleWithRequiredData: IOrganisationUnit = {
  id: 26767,
  uid: 'Program Kid',
  name: 'Soap',
  organisationUnitType: OrganisationUnitType['GOV'],
};

export const sampleWithPartialData: IOrganisationUnit = {
  id: 28330,
  uid: 'Spurs stati',
  name: 'application Fresh Producer',
  shortName: 'Regional monitoring Industrial',
  updated: dayjs('2022-11-19T23:45'),
  openingDate: dayjs('2022-11-20'),
  comment: 'e-business Colon Architect',
  address: 'cross-platform as Concrete',
  organisationUnitType: OrganisationUnitType['DISTRICT'],
  inactive: false,
};

export const sampleWithFullData: IOrganisationUnit = {
  id: 77110,
  uid: 'XSS bypass',
  code: 'array',
  name: 'Account deposit',
  shortName: 'cyan redefine',
  created: dayjs('2022-11-20T01:24'),
  updated: dayjs('2022-11-20T04:11'),
  path: 'XSS Technician Tuna',
  hierarchyLevel: 51771,
  openingDate: dayjs('2022-11-20'),
  comment: 'Salvador',
  closedDate: dayjs('2022-11-20'),
  url: 'http://jessyca.biz',
  contactPerson: 'Chips Regional',
  address: 'Internal SMTP',
  email: 'Florencio.Ortiz61@yahoo.com',
  phoneNumber: "Pa'anga",
  organisationUnitType: OrganisationUnitType['GOV'],
  inactive: true,
};

export const sampleWithNewData: NewOrganisationUnit = {
  uid: 'Landing acc',
  name: 'Borders Stream',
  organisationUnitType: OrganisationUnitType['SUB_VILLAGE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
