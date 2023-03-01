import dayjs from 'dayjs/esm';

import { ITrackedEntityTypeAttribute, NewTrackedEntityTypeAttribute } from './tracked-entity-type-attribute.model';

export const sampleWithRequiredData: ITrackedEntityTypeAttribute = {
  id: 63010,
  uid: 'Intelligent',
};

export const sampleWithPartialData: ITrackedEntityTypeAttribute = {
  id: 77667,
  uid: 'maroon Kids',
  created: dayjs('2022-11-20T10:07'),
  updated: dayjs('2022-11-20T07:06'),
  displayInList: false,
  mandatory: false,
};

export const sampleWithFullData: ITrackedEntityTypeAttribute = {
  id: 88237,
  uid: 'Response',
  code: 'input',
  created: dayjs('2022-11-20T11:39'),
  updated: dayjs('2022-11-20T07:23'),
  displayInList: true,
  mandatory: true,
  searchable: true,
};

export const sampleWithNewData: NewTrackedEntityTypeAttribute = {
  uid: 'Electronics',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
