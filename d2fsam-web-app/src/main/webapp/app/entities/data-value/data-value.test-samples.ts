import dayjs from 'dayjs/esm';

import { AuditType } from 'app/entities/enumerations/audit-type.model';

import { IDataValue, NewDataValue } from './data-value.model';

export const sampleWithRequiredData: IDataValue = {
  id: 20981,
};

export const sampleWithPartialData: IDataValue = {
  id: 10234,
  storedBy: 'installation',
};

export const sampleWithFullData: IDataValue = {
  id: 70360,
  value: 'Sleek monitor monitor',
  storedBy: 'quantify Timor-Leste',
  created: dayjs('2022-11-20T13:45'),
  updated: dayjs('2022-11-19T23:27'),
  comment: 'Marketing',
  followup: false,
  deleted: true,
  auditType: AuditType['DELETE'],
};

export const sampleWithNewData: NewDataValue = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
