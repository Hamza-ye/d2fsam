import dayjs from 'dayjs/esm';

import { PeriodLabel } from 'app/entities/enumerations/period-label.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

import { IProgramInstance, NewProgramInstance } from './program-instance.model';

export const sampleWithRequiredData: IProgramInstance = {
  id: 86804,
  uid: 'extranet Lo',
};

export const sampleWithPartialData: IProgramInstance = {
  id: 99477,
  uid: 'enable matr',
  uuid: 'e03885ee-18c7-4d85-9f92-9660968eb022',
  created: dayjs('2022-11-20T09:20'),
  lastSynchronized: dayjs('2022-11-20T13:27'),
  incidentDate: dayjs('2022-11-20T08:47'),
  enrollmentDate: dayjs('2022-11-20T00:13'),
  periodLabel: PeriodLabel['P4'],
  storedBy: 'Buckinghamshire',
  completedBy: 'Buckinghamshire',
  completedDate: dayjs('2022-11-20T10:34'),
  deleted: true,
};

export const sampleWithFullData: IProgramInstance = {
  id: 35853,
  uid: 'Director',
  uuid: '9b364dd4-2f94-4308-a286-f81ba1b3cb66',
  created: dayjs('2022-11-20T11:03'),
  updated: dayjs('2022-11-20T04:15'),
  createdAtClient: dayjs('2022-11-20T06:22'),
  updatedAtClient: dayjs('2022-11-20T03:12'),
  lastSynchronized: dayjs('2022-11-20T03:25'),
  incidentDate: dayjs('2022-11-20T06:12'),
  enrollmentDate: dayjs('2022-11-20T15:03'),
  periodLabel: PeriodLabel['P5'],
  endDate: dayjs('2022-11-20T03:01'),
  status: EventStatus['OVERDUE'],
  storedBy: 'Frozen Metal bandwidth-monitored',
  completedBy: 'Regional UIC-Franc Intranet',
  completedDate: dayjs('2022-11-20T09:52'),
  followup: true,
  deleted: true,
  deletedAt: dayjs('2022-11-19T21:11'),
};

export const sampleWithNewData: NewProgramInstance = {
  uid: 'Books',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
