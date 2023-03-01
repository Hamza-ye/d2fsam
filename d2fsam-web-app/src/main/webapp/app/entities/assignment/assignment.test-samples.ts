import dayjs from 'dayjs/esm';

import { PeriodLabel } from 'app/entities/enumerations/period-label.model';
import { TargetSource } from 'app/entities/enumerations/target-source.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

import { IAssignment, NewAssignment } from './assignment.model';

export const sampleWithRequiredData: IAssignment = {
  id: 46201,
  uid: 'Spain silve',
};

export const sampleWithPartialData: IAssignment = {
  id: 95163,
  uid: 'calculating',
  description: 'Engineer',
  startDate: dayjs('2022-11-20'),
  startPeriod: PeriodLabel['P11'],
  deletedAt: dayjs('2022-11-20T19:47'),
};

export const sampleWithFullData: IAssignment = {
  id: 45315,
  uid: 'monitor',
  code: 'Awesome drive',
  created: dayjs('2022-11-20T17:52'),
  updated: dayjs('2022-11-20T11:47'),
  description: 'Rupee viral Mississippi',
  startDate: dayjs('2022-11-20'),
  startPeriod: PeriodLabel['P10'],
  targetSource: TargetSource['DEMOGRAPHIC'],
  status: EventStatus['VISITED'],
  deleted: false,
  deletedAt: dayjs('2022-11-20T14:32'),
};

export const sampleWithNewData: NewAssignment = {
  uid: 'customized',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
