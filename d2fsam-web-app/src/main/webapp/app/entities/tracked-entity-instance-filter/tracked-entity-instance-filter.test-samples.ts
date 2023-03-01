import dayjs from 'dayjs/esm';

import { EventStatus } from 'app/entities/enumerations/event-status.model';

import { ITrackedEntityInstanceFilter, NewTrackedEntityInstanceFilter } from './tracked-entity-instance-filter.model';

export const sampleWithRequiredData: ITrackedEntityInstanceFilter = {
  id: 20653,
  uid: 'ivory',
};

export const sampleWithPartialData: ITrackedEntityInstanceFilter = {
  id: 8943,
  uid: '1080p Produ',
  name: 'Chips Tuna fuchsia',
  description: 'Shoes Money',
};

export const sampleWithFullData: ITrackedEntityInstanceFilter = {
  id: 61343,
  uid: 'Steel minds',
  code: 'Tuna HTTP orange',
  name: '1080p ADP index',
  created: dayjs('2022-11-20T15:12'),
  updated: dayjs('2022-11-20T19:38'),
  description: 'Branding',
  sortOrder: 79592,
  enrollmentStatus: EventStatus['SKIPPED'],
};

export const sampleWithNewData: NewTrackedEntityInstanceFilter = {
  uid: 'Practical L',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
