import dayjs from 'dayjs/esm';

import { ITrackedEntityAttributeValue, NewTrackedEntityAttributeValue } from './tracked-entity-attribute-value.model';

export const sampleWithRequiredData: ITrackedEntityAttributeValue = {
  id: 33548,
};

export const sampleWithPartialData: ITrackedEntityAttributeValue = {
  id: 150,
  encryptedValue: 'orchid Plastic Account',
  plainValue: 'Virginia Sausages help-desk',
  value: 'next-generation Palestinian capability',
  created: dayjs('2022-11-20T01:31'),
};

export const sampleWithFullData: ITrackedEntityAttributeValue = {
  id: 71257,
  encryptedValue: 'up Hampshire',
  plainValue: 'Devolved',
  value: 'Analyst',
  created: dayjs('2022-11-20T15:23'),
  updated: dayjs('2022-11-20T15:22'),
  storedBy: 'Orchestrator Persevering architectures',
};

export const sampleWithNewData: NewTrackedEntityAttributeValue = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
