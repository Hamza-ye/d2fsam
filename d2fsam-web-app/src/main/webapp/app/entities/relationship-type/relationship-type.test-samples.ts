import dayjs from 'dayjs/esm';

import { IRelationshipType, NewRelationshipType } from './relationship-type.model';

export const sampleWithRequiredData: IRelationshipType = {
  id: 50880,
  uid: 'task-force',
  name: 'Gloves',
};

export const sampleWithPartialData: IRelationshipType = {
  id: 79053,
  uid: 'Gorgeous Re',
  name: 'THX USB',
  updated: dayjs('2022-11-20T09:37'),
  toFromName: 'Sports navigate backing',
  referral: true,
};

export const sampleWithFullData: IRelationshipType = {
  id: 89439,
  uid: 'Coordinator',
  code: 'Planner Organized Guatemala',
  name: 'responsive matrix',
  created: dayjs('2022-11-20T07:04'),
  updated: dayjs('2022-11-20T12:42'),
  description: 'Ergonomic',
  bidirectional: false,
  fromToName: 'coherent Cape Cyprus',
  toFromName: 'Isle interface Tactics',
  referral: true,
};

export const sampleWithNewData: NewRelationshipType = {
  uid: 'Baby',
  name: 'Cheese Bedfordshire',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
