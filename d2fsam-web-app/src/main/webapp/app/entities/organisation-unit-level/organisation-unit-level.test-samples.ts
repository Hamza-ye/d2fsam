import dayjs from 'dayjs/esm';

import { IOrganisationUnitLevel, NewOrganisationUnitLevel } from './organisation-unit-level.model';

export const sampleWithRequiredData: IOrganisationUnitLevel = {
  id: 91080,
  uid: 'silver appl',
};

export const sampleWithPartialData: IOrganisationUnitLevel = {
  id: 1299,
  uid: 'Loan Fiji',
  code: 'best-of-breed',
  created: dayjs('2022-11-20T01:00'),
  updated: dayjs('2022-11-20T05:40'),
  level: 89967,
};

export const sampleWithFullData: IOrganisationUnitLevel = {
  id: 75081,
  uid: 'Central nav',
  code: 'Fundamental Wooden',
  name: 'matrix niches',
  created: dayjs('2022-11-19T23:56'),
  updated: dayjs('2022-11-20T04:58'),
  level: 97666,
  offlineLevels: 23112,
};

export const sampleWithNewData: NewOrganisationUnitLevel = {
  uid: 'SAS Awesome',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
