import dayjs from 'dayjs/esm';

import { IProgramOwnershipHistory, NewProgramOwnershipHistory } from './program-ownership-history.model';

export const sampleWithRequiredData: IProgramOwnershipHistory = {
  id: 46251,
};

export const sampleWithPartialData: IProgramOwnershipHistory = {
  id: 67317,
  startDate: dayjs('2022-11-20T08:35'),
};

export const sampleWithFullData: IProgramOwnershipHistory = {
  id: 11474,
  createdBy: 'deploy',
  startDate: dayjs('2022-11-20T07:07'),
  endDate: dayjs('2022-11-20T06:38'),
};

export const sampleWithNewData: NewProgramOwnershipHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
