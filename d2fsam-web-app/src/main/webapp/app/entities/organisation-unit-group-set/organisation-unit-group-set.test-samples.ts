import dayjs from 'dayjs/esm';

import { IOrganisationUnitGroupSet, NewOrganisationUnitGroupSet } from './organisation-unit-group-set.model';

export const sampleWithRequiredData: IOrganisationUnitGroupSet = {
  id: 18572,
  uid: 'Wisconsin S',
  name: 'mobile open-source up',
};

export const sampleWithPartialData: IOrganisationUnitGroupSet = {
  id: 26429,
  uid: 'solid Nevad',
  code: 'Brunei ROI',
  name: 'non-volatile Bedfordshire',
  includeSubhierarchyInAnalytics: true,
};

export const sampleWithFullData: IOrganisationUnitGroupSet = {
  id: 16605,
  uid: 'redundant G',
  code: 'metrics',
  name: 'service-desk withdrawal',
  created: dayjs('2022-11-20T19:04'),
  updated: dayjs('2022-11-19T23:56'),
  compulsory: true,
  includeSubhierarchyInAnalytics: false,
  inactive: true,
};

export const sampleWithNewData: NewOrganisationUnitGroupSet = {
  uid: 'Maryland Pl',
  name: 'Handmade Networked',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
