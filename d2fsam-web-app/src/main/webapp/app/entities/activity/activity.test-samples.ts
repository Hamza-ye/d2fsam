import dayjs from 'dayjs/esm';

import { IActivity, NewActivity } from './activity.model';

export const sampleWithRequiredData: IActivity = {
  id: 68021,
  uid: 'Marketing P',
  name: 'up Optimization',
  startDate: dayjs('2022-11-20'),
  endDate: dayjs('2022-11-20'),
};

export const sampleWithPartialData: IActivity = {
  id: 79813,
  uid: 'index',
  name: 'connect',
  shortName: 'Sports encompassing extend',
  description: 'Concrete',
  startDate: dayjs('2022-11-20'),
  endDate: dayjs('2022-11-20'),
  hidden: false,
  order: 65324,
};

export const sampleWithFullData: IActivity = {
  id: 81031,
  uid: 'bandwidth o',
  code: 'streamline',
  name: 'compress Computers Senegal',
  shortName: 'payment deposit',
  description: 'Bolivia Washington',
  created: dayjs('2022-11-19T22:17'),
  updated: dayjs('2022-11-20T06:25'),
  startDate: dayjs('2022-11-20'),
  endDate: dayjs('2022-11-20'),
  hidden: true,
  order: 46014,
  inactive: true,
};

export const sampleWithNewData: NewActivity = {
  uid: 'Ways levera',
  name: 'Designer',
  startDate: dayjs('2022-11-20'),
  endDate: dayjs('2022-11-20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
