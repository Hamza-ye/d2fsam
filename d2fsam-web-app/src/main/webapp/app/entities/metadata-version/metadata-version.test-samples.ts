import dayjs from 'dayjs/esm';

import { VersionType } from 'app/entities/enumerations/version-type.model';

import { IMetadataVersion, NewMetadataVersion } from './metadata-version.model';

export const sampleWithRequiredData: IMetadataVersion = {
  id: 92195,
};

export const sampleWithPartialData: IMetadataVersion = {
  id: 3504,
  uid: 'Wooden invo',
  created: dayjs('2022-11-20T10:15'),
  updated: dayjs('2022-11-20T03:34'),
  importDate: dayjs('2022-11-20T01:54'),
  type: VersionType['BEST_EFFORT'],
  hashCode: 'Avon',
};

export const sampleWithFullData: IMetadataVersion = {
  id: 23559,
  uid: 'Soft firewa',
  code: 'connect Generic Bulgarian',
  created: dayjs('2022-11-20T06:18'),
  updated: dayjs('2022-11-20T07:06'),
  importDate: dayjs('2022-11-20T14:36'),
  type: VersionType['ATOMIC'],
  hashCode: 'systemic Cotton',
};

export const sampleWithNewData: NewMetadataVersion = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
