import dayjs from 'dayjs/esm';

import { IProgramTempOwner, NewProgramTempOwner } from './program-temp-owner.model';

export const sampleWithRequiredData: IProgramTempOwner = {
  id: 42939,
};

export const sampleWithPartialData: IProgramTempOwner = {
  id: 45037,
  reason: 'robust',
};

export const sampleWithFullData: IProgramTempOwner = {
  id: 11362,
  reason: 'Tala extranet Checking',
  validTill: dayjs('2022-11-19T23:00'),
};

export const sampleWithNewData: NewProgramTempOwner = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
