import dayjs from 'dayjs/esm';

import { IOption, NewOption } from './option.model';

export const sampleWithRequiredData: IOption = {
  id: 71232,
  uid: 'Liaison Fre',
  name: 'yellow SAS platforms',
};

export const sampleWithPartialData: IOption = {
  id: 32415,
  uid: 'composite G',
  code: 'discrete',
  name: 'Taiwan',
  updated: dayjs('2022-11-20T16:26'),
  sortOrder: 81098,
};

export const sampleWithFullData: IOption = {
  id: 92552,
  uid: 'Creek',
  code: 'impactful',
  name: 'Texas',
  created: dayjs('2022-11-20T02:57'),
  updated: dayjs('2022-11-19T22:03'),
  description: 'States',
  sortOrder: 86485,
};

export const sampleWithNewData: NewOption = {
  uid: 'Dinar',
  name: 'Account brand utilisation',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
