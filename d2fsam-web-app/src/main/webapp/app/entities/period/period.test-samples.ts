import dayjs from 'dayjs/esm';

import { IPeriod, NewPeriod } from './period.model';

export const sampleWithRequiredData: IPeriod = {
  id: 38959,
  startDate: dayjs('2022-11-19'),
  endDate: dayjs('2022-11-20'),
};

export const sampleWithPartialData: IPeriod = {
  id: 56386,
  name: 'streamline',
  startDate: dayjs('2022-11-19'),
  endDate: dayjs('2022-11-20'),
};

export const sampleWithFullData: IPeriod = {
  id: 8109,
  name: 'Innovative GB bandwidth-monitored',
  startDate: dayjs('2022-11-20'),
  endDate: dayjs('2022-11-20'),
};

export const sampleWithNewData: NewPeriod = {
  startDate: dayjs('2022-11-20'),
  endDate: dayjs('2022-11-19'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
