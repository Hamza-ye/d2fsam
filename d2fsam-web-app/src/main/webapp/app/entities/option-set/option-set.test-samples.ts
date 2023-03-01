import dayjs from 'dayjs/esm';

import { ValueType } from 'app/entities/enumerations/value-type.model';

import { IOptionSet, NewOptionSet } from './option-set.model';

export const sampleWithRequiredData: IOptionSet = {
  id: 56291,
  uid: 'Buckinghams',
  name: 'concept',
};

export const sampleWithPartialData: IOptionSet = {
  id: 11807,
  uid: 'Ergonomic I',
  code: 'Heard',
  name: 'quantifying Soft',
  valueType: ValueType['EMAIL'],
  version: 50931,
};

export const sampleWithFullData: IOptionSet = {
  id: 17362,
  uid: 'West Ergono',
  code: 'Steel Lead Computers',
  name: 'Producer Technician',
  created: dayjs('2022-11-19T21:21'),
  updated: dayjs('2022-11-20T06:32'),
  valueType: ValueType['URL'],
  version: 87932,
};

export const sampleWithNewData: NewOptionSet = {
  uid: 'Utah Direct',
  name: 'Handmade Keyboard',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
