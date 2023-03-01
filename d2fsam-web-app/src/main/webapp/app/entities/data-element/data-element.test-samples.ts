import dayjs from 'dayjs/esm';

import { ValueType } from 'app/entities/enumerations/value-type.model';
import { AggregationType } from 'app/entities/enumerations/aggregation-type.model';

import { IDataElement, NewDataElement } from './data-element.model';

export const sampleWithRequiredData: IDataElement = {
  id: 38488,
  uid: 'Ball',
  name: 'Product Points',
};

export const sampleWithPartialData: IDataElement = {
  id: 76997,
  uid: 'Jamaica sti',
  created: dayjs('2022-11-20T00:59'),
  updated: dayjs('2022-11-20T06:49'),
  name: 'Pizza',
  valueType: ValueType['TRACKER_ASSOCIATE'],
  aggregationType: AggregationType['CUSTOM'],
  displayInListNoProgram: true,
  fieldMask: 'Lek',
  skipSynchronization: false,
  confidential: false,
};

export const sampleWithFullData: IDataElement = {
  id: 61783,
  uid: 'indigo',
  code: 'indexing discrete enhance',
  created: dayjs('2022-11-20T15:44'),
  updated: dayjs('2022-11-20T13:38'),
  name: 'invoice',
  description: 'enhance',
  valueType: ValueType['GEOJSON'],
  aggregationType: AggregationType['LAST_AVERAGE_ORG_UNIT'],
  displayInListNoProgram: false,
  zeroIsSignificant: true,
  mandatory: false,
  uniqueAttribute: false,
  fieldMask: 'process Pennsylvania Direct',
  orgunitScope: true,
  skipSynchronization: false,
  confidential: false,
};

export const sampleWithNewData: NewDataElement = {
  uid: 'withdrawal ',
  name: 'Handmade Engineer Refined',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
