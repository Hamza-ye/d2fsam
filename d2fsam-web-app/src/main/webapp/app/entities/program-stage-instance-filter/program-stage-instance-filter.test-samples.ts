import dayjs from 'dayjs/esm';

import { IProgramStageInstanceFilter, NewProgramStageInstanceFilter } from './program-stage-instance-filter.model';

export const sampleWithRequiredData: IProgramStageInstanceFilter = {
  id: 97622,
  uid: 'stable Infr',
};

export const sampleWithPartialData: IProgramStageInstanceFilter = {
  id: 8457,
  uid: 'Cloned',
};

export const sampleWithFullData: IProgramStageInstanceFilter = {
  id: 33330,
  uid: 'Application',
  code: 'Account withdrawal Fish',
  name: 'Guatemala Pakistan Checking',
  created: dayjs('2022-11-20T04:18'),
  updated: dayjs('2022-11-20T10:40'),
  description: 'expedite Dinar matrix',
  program: 'Hawaii Cambridgeshire Associate',
  programStage: 'transform',
};

export const sampleWithNewData: NewProgramStageInstanceFilter = {
  uid: 'pixel e-ser',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
