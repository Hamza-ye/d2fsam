import dayjs from 'dayjs/esm';

import { IProgramTempOwnershipAudit, NewProgramTempOwnershipAudit } from './program-temp-ownership-audit.model';

export const sampleWithRequiredData: IProgramTempOwnershipAudit = {
  id: 69420,
};

export const sampleWithPartialData: IProgramTempOwnershipAudit = {
  id: 1223,
  reason: 'Persistent',
};

export const sampleWithFullData: IProgramTempOwnershipAudit = {
  id: 44829,
  reason: 'blue',
  created: dayjs('2022-11-20T07:55'),
  accessedBy: 'Director',
};

export const sampleWithNewData: NewProgramTempOwnershipAudit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
