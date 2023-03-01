import dayjs from 'dayjs/esm';

import { IDatastoreEntry, NewDatastoreEntry } from './datastore-entry.model';

export const sampleWithRequiredData: IDatastoreEntry = {
  id: 7583,
};

export const sampleWithPartialData: IDatastoreEntry = {
  id: 22421,
};

export const sampleWithFullData: IDatastoreEntry = {
  id: 25409,
  uid: 'deliverable',
  code: 'deposit',
  created: dayjs('2022-11-20T02:15'),
  updated: dayjs('2022-11-20T15:51'),
  key: 'Jewelery',
  namespace: 'grey',
  encrypted: false,
};

export const sampleWithNewData: NewDatastoreEntry = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
