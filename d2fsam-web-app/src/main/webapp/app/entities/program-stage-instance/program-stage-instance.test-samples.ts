import dayjs from 'dayjs/esm';

import { EventStatus } from 'app/entities/enumerations/event-status.model';

import { IProgramStageInstance, NewProgramStageInstance } from './program-stage-instance.model';

export const sampleWithRequiredData: IProgramStageInstance = {
  id: 96458,
  uid: 'Concrete pa',
};

export const sampleWithPartialData: IProgramStageInstance = {
  id: 67378,
  uid: 'Home',
  code: 'navigate salmon Mall',
  updated: dayjs('2022-11-20T19:48'),
  lastSynchronized: dayjs('2022-11-20T04:20'),
  dueDate: dayjs('2022-11-20T06:58'),
  status: EventStatus['SCHEDULE'],
  completedDate: dayjs('2022-11-20T13:09'),
  deleted: true,
};

export const sampleWithFullData: IProgramStageInstance = {
  id: 15663,
  uid: 'Forge Shill',
  uuid: 'ccda92ba-0a0f-4999-b977-c6baf2c963b3',
  code: 'transmitting Tennessee',
  created: dayjs('2022-11-20T19:49'),
  updated: dayjs('2022-11-20T15:42'),
  createdAtClient: dayjs('2022-11-19T20:51'),
  updatedAtClient: dayjs('2022-11-20T03:14'),
  lastSynchronized: dayjs('2022-11-20T19:54'),
  dueDate: dayjs('2022-11-20T07:14'),
  executionDate: dayjs('2022-11-20T12:34'),
  status: EventStatus['REVIEWED'],
  storedBy: 'Fundamental Implemented e-tailers',
  completedBy: 'neural Peru',
  completedDate: dayjs('2022-11-20T01:22'),
  deleted: true,
  deletedAt: dayjs('2022-11-19T21:05'),
};

export const sampleWithNewData: NewProgramStageInstance = {
  uid: 'Toys',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
