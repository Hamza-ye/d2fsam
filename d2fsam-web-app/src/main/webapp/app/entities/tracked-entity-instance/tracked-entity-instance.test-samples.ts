import dayjs from 'dayjs/esm';

import { FeatureType } from 'app/entities/enumerations/feature-type.model';

import { ITrackedEntityInstance, NewTrackedEntityInstance } from './tracked-entity-instance.model';

export const sampleWithRequiredData: ITrackedEntityInstance = {
  id: 32291,
  uid: 'Expanded Pl',
};

export const sampleWithPartialData: ITrackedEntityInstance = {
  id: 58167,
  uid: 'Cambridgesh',
  code: 'generate circuit Shoes',
  created: dayjs('2022-11-20T16:19'),
  updated: dayjs('2022-11-20T08:22'),
  createdAtClient: dayjs('2022-11-20T06:19'),
  updatedAtClient: dayjs('2022-11-20T13:04'),
  coordinates: 'Multi-layered',
  potentialDuplicate: true,
  storedBy: 'IB Cambridgeshire',
  inactive: true,
};

export const sampleWithFullData: ITrackedEntityInstance = {
  id: 44867,
  uid: 'Bike primar',
  uuid: 'ee9a6a07-4532-4185-8209-11905cc783f7',
  code: 'Chips tan Chips',
  created: dayjs('2022-11-20T11:52'),
  updated: dayjs('2022-11-20T06:45'),
  createdAtClient: dayjs('2022-11-20T07:28'),
  updatedAtClient: dayjs('2022-11-20T03:56'),
  lastSynchronized: dayjs('2022-11-20T13:24'),
  featureType: FeatureType['MULTI_POLYGON'],
  coordinates: 'Granite Account withdrawal',
  potentialDuplicate: true,
  deleted: true,
  storedBy: 'Costa infrastructures',
  inactive: true,
};

export const sampleWithNewData: NewTrackedEntityInstance = {
  uid: 'open',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
