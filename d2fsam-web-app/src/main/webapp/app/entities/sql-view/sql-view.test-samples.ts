import dayjs from 'dayjs/esm';

import { SqlViewType } from 'app/entities/enumerations/sql-view-type.model';
import { CacheStrategy } from 'app/entities/enumerations/cache-strategy.model';

import { ISqlView, NewSqlView } from './sql-view.model';

export const sampleWithRequiredData: ISqlView = {
  id: 95486,
  uid: 'Ball transf',
  name: 'systematic',
};

export const sampleWithPartialData: ISqlView = {
  id: 26075,
  uid: 'Right-sized',
  name: 'Cambridgeshire Bangladesh viral',
  created: dayjs('2022-11-20T17:25'),
  updated: dayjs('2022-11-19T23:13'),
  description: 'ivory',
  sqlQuery: 'Unbranded',
  type: SqlViewType['MATERIALIZED_VIEW'],
};

export const sampleWithFullData: ISqlView = {
  id: 68537,
  uid: 'JSON Accoun',
  code: 'optimal',
  name: 'USB',
  created: dayjs('2022-11-20T07:13'),
  updated: dayjs('2022-11-20T13:53'),
  description: 'Hat',
  sqlQuery: 'Rubber global compressing',
  type: SqlViewType['VIEW'],
  cacheStrategy: CacheStrategy['NO_CACHE'],
};

export const sampleWithNewData: NewSqlView = {
  uid: 'Account Int',
  name: 'application Loan database',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
