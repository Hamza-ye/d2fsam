import dayjs from 'dayjs/esm';

import { FeatureType } from 'app/entities/enumerations/feature-type.model';

import { ITrackedEntityType, NewTrackedEntityType } from './tracked-entity-type.model';

export const sampleWithRequiredData: ITrackedEntityType = {
  id: 13171,
  uid: 'Credit empo',
  name: 'pink',
};

export const sampleWithPartialData: ITrackedEntityType = {
  id: 20947,
  uid: 'Gorgeous cy',
  code: 'redefine',
  name: 'generating Massachusetts',
  description: 'payment bricks-and-clicks',
  maxTeiCountToReturn: 39731,
};

export const sampleWithFullData: ITrackedEntityType = {
  id: 94261,
  uid: 'Extended sy',
  code: 'Hat pixel Avon',
  created: dayjs('2022-11-20T06:13'),
  updated: dayjs('2022-11-20T00:04'),
  name: 'Shoes',
  description: 'Forward',
  maxTeiCountToReturn: 32133,
  allowAuditLog: true,
  featureType: FeatureType['MULTI_POLYGON'],
};

export const sampleWithNewData: NewTrackedEntityType = {
  uid: 'Jewelery',
  name: 'bypassing demand-driven Intelligent',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
