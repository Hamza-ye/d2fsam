import dayjs from 'dayjs/esm';

import { IUserGroup, NewUserGroup } from './user-group.model';

export const sampleWithRequiredData: IUserGroup = {
  id: 42880,
  uid: 'deposit',
  name: 'Plastic revolutionary Account',
};

export const sampleWithPartialData: IUserGroup = {
  id: 30146,
  uid: 'Awesome Uru',
  code: 'Savings',
  name: 'Kazakhstan Gambia',
  activeTo: dayjs('2022-11-20T09:04'),
};

export const sampleWithFullData: IUserGroup = {
  id: 81655,
  uid: 'portals',
  code: 'salmon impactful',
  name: 'partnerships',
  uuid: 'd55f19b8-d249-4d39-b176-e65e665218f0',
  created: dayjs('2022-11-20T03:26'),
  updated: dayjs('2022-11-19T22:15'),
  activeFrom: dayjs('2022-11-20T00:53'),
  activeTo: dayjs('2022-11-19T23:38'),
  inactive: false,
};

export const sampleWithNewData: NewUserGroup = {
  uid: 'maximize Pr',
  name: 'radical Total Cambridgeshire',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
