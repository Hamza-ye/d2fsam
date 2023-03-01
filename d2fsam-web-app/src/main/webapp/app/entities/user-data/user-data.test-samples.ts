import dayjs from 'dayjs/esm';

import { IUserData, NewUserData } from './user-data.model';

export const sampleWithRequiredData: IUserData = {
  id: 72158,
};

export const sampleWithPartialData: IUserData = {
  id: 82420,
  uid: 'Fresh Maria',
  code: 'black Generic',
};

export const sampleWithFullData: IUserData = {
  id: 71296,
  uid: 'Officer Gam',
  code: 'AGP',
  name: 'Account Rubber one-to-one',
  created: dayjs('2022-11-20T13:17'),
  updated: dayjs('2022-11-20T16:26'),
  uuid: '15dace88-5e98-455b-a83d-eedacf122bd0',
  inactive: true,
};

export const sampleWithNewData: NewUserData = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
