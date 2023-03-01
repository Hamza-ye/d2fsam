import { IPeriodType, NewPeriodType } from './period-type.model';

export const sampleWithRequiredData: IPeriodType = {
  id: 96939,
};

export const sampleWithPartialData: IPeriodType = {
  id: 98766,
};

export const sampleWithFullData: IPeriodType = {
  id: 91205,
  name: 'Turnpike Architect',
};

export const sampleWithNewData: NewPeriodType = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
