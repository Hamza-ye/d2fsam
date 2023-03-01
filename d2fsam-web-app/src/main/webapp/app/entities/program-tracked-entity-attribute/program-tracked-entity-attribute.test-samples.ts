import dayjs from 'dayjs/esm';

import { IProgramTrackedEntityAttribute, NewProgramTrackedEntityAttribute } from './program-tracked-entity-attribute.model';

export const sampleWithRequiredData: IProgramTrackedEntityAttribute = {
  id: 59575,
  uid: 'Wyoming SCS',
};

export const sampleWithPartialData: IProgramTrackedEntityAttribute = {
  id: 47939,
  uid: 'partnership',
  code: 'Views',
  sortOrder: 55422,
  mandatory: false,
};

export const sampleWithFullData: IProgramTrackedEntityAttribute = {
  id: 54822,
  uid: 'Stand-alone',
  code: 'Latvia override Senior',
  created: dayjs('2022-11-20T12:17'),
  updated: dayjs('2022-11-19T20:58'),
  displayInList: true,
  sortOrder: 47422,
  mandatory: true,
  allowFutureDate: false,
  searchable: true,
  defaultValue: 'Lane calculate',
};

export const sampleWithNewData: NewProgramTrackedEntityAttribute = {
  uid: 'circuit gre',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
