import dayjs from 'dayjs/esm';

import { IDemographicDataSource, NewDemographicDataSource } from './demographic-data-source.model';

export const sampleWithRequiredData: IDemographicDataSource = {
  id: 73327,
  uid: 'Azerbaijan ',
  name: 'hacking robust blue',
};

export const sampleWithPartialData: IDemographicDataSource = {
  id: 42882,
  uid: 'Distributed',
  code: 'teal Grenadines',
  name: 'FTP Account calculating',
};

export const sampleWithFullData: IDemographicDataSource = {
  id: 10901,
  uid: 'Computer po',
  code: 'override magnetic Cambridgeshire',
  name: 'foreground',
  created: dayjs('2022-11-20T18:10'),
  updated: dayjs('2022-11-20T14:29'),
};

export const sampleWithNewData: NewDemographicDataSource = {
  uid: 'transmit',
  name: 'Switchable',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
