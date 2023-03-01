import dayjs from 'dayjs/esm';

import { IExternalFileResource, NewExternalFileResource } from './external-file-resource.model';

export const sampleWithRequiredData: IExternalFileResource = {
  id: 51144,
  uid: 'green Group',
};

export const sampleWithPartialData: IExternalFileResource = {
  id: 57475,
  uid: 'Concrete us',
  name: 'encoding Principal',
  created: dayjs('2022-11-19T22:13'),
  updated: dayjs('2022-11-20T09:18'),
};

export const sampleWithFullData: IExternalFileResource = {
  id: 38311,
  uid: 'Handcrafted',
  code: 'vortals',
  name: 'schemas silver bus',
  created: dayjs('2022-11-20T06:52'),
  updated: dayjs('2022-11-20T02:04'),
  accessToken: 'Aruba wireless Analyst',
  expires: dayjs('2022-11-20T02:50'),
};

export const sampleWithNewData: NewExternalFileResource = {
  uid: 'feed',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
