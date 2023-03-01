import dayjs from 'dayjs/esm';

import { IChv, NewChv } from './chv.model';

export const sampleWithRequiredData: IChv = {
  id: 16116,
  uid: 'Universal',
};

export const sampleWithPartialData: IChv = {
  id: 55759,
  uid: 'black Stree',
  created: dayjs('2022-11-20T05:52'),
  updated: dayjs('2022-11-20T10:26'),
  withdrawn: true,
  dateJoined: dayjs('2022-11-20'),
  inactive: false,
};

export const sampleWithFullData: IChv = {
  id: 98533,
  uid: 'bleeding-ed',
  code: 'Shirt',
  created: dayjs('2022-11-20T15:49'),
  updated: dayjs('2022-11-19T23:49'),
  withdrawn: false,
  dateJoined: dayjs('2022-11-20'),
  dateWithdrawn: dayjs('2022-11-20'),
  name: 'bandwidth back web-readiness',
  description: 'synergize maximize Ergonomic',
  inactive: true,
};

export const sampleWithNewData: NewChv = {
  uid: 'parsing',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
