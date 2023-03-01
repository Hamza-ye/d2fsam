import dayjs from 'dayjs/esm';

import { AuditType } from 'app/entities/enumerations/audit-type.model';

import { ITrackedEntityDataValueAudit, NewTrackedEntityDataValueAudit } from './tracked-entity-data-value-audit.model';

export const sampleWithRequiredData: ITrackedEntityDataValueAudit = {
  id: 41997,
};

export const sampleWithPartialData: ITrackedEntityDataValueAudit = {
  id: 50585,
  value: 'Internal',
  created: dayjs('2022-11-20T12:02'),
  updated: dayjs('2022-11-20T08:22'),
  providedElsewhere: false,
  auditType: AuditType['READ'],
};

export const sampleWithFullData: ITrackedEntityDataValueAudit = {
  id: 13251,
  value: 'dedicated Fresh invoice',
  created: dayjs('2022-11-20T17:07'),
  updated: dayjs('2022-11-20T19:54'),
  modifiedBy: 'Bahamas',
  providedElsewhere: false,
  auditType: AuditType['DELETE'],
};

export const sampleWithNewData: NewTrackedEntityDataValueAudit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
