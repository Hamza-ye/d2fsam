import dayjs from 'dayjs/esm';

import { IRelationship, NewRelationship } from './relationship.model';

export const sampleWithRequiredData: IRelationship = {
  id: 63972,
  uid: 'cyan withdr',
};

export const sampleWithPartialData: IRelationship = {
  id: 89159,
  uid: 'USB service',
  code: 'Lock',
  updated: dayjs('2022-11-20T05:32'),
  invertedKey: 'Soft backing Optimization',
  deleted: false,
};

export const sampleWithFullData: IRelationship = {
  id: 77977,
  uid: 'Berkshire',
  code: 'Handmade HDD',
  created: dayjs('2022-11-20T10:58'),
  updated: dayjs('2022-11-20T14:56'),
  formName: 'Chips Grocery protocol',
  description: 'neural-net Fresh deposit',
  key: 'Outdoors',
  invertedKey: 'Account',
  deleted: false,
};

export const sampleWithNewData: NewRelationship = {
  uid: 'Designer',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
