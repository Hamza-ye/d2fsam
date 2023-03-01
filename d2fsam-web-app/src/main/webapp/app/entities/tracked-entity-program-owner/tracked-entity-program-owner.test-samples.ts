import dayjs from 'dayjs/esm';

import { ITrackedEntityProgramOwner, NewTrackedEntityProgramOwner } from './tracked-entity-program-owner.model';

export const sampleWithRequiredData: ITrackedEntityProgramOwner = {
  id: 34084,
};

export const sampleWithPartialData: ITrackedEntityProgramOwner = {
  id: 48497,
  updated: dayjs('2022-11-20T05:40'),
  createdBy: 'Intelligent North grey',
};

export const sampleWithFullData: ITrackedEntityProgramOwner = {
  id: 71166,
  created: dayjs('2022-11-20T02:52'),
  updated: dayjs('2022-11-20T09:18'),
  createdBy: 'exploit calculate',
};

export const sampleWithNewData: NewTrackedEntityProgramOwner = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
