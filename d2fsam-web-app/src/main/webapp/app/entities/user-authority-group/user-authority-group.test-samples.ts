import dayjs from 'dayjs/esm';

import { IUserAuthorityGroup, NewUserAuthorityGroup } from './user-authority-group.model';

export const sampleWithRequiredData: IUserAuthorityGroup = {
  id: 31005,
};

export const sampleWithPartialData: IUserAuthorityGroup = {
  id: 34672,
  name: 'Cambridgeshire target Senegal',
  description: 'olive zero',
  created: dayjs('2022-11-20T09:21'),
  updated: dayjs('2022-11-20T09:28'),
};

export const sampleWithFullData: IUserAuthorityGroup = {
  id: 83263,
  uid: 'Zambia Hat ',
  code: 'Djibouti',
  name: 'application lavender navigate',
  description: 'Tools maroon',
  created: dayjs('2022-11-20T15:10'),
  updated: dayjs('2022-11-20T05:36'),
  inactive: false,
};

export const sampleWithNewData: NewUserAuthorityGroup = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
