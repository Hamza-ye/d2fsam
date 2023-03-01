import dayjs from 'dayjs/esm';

import { AuditType } from 'app/entities/enumerations/audit-type.model';

import { ITrackedEntityAttributeValueAudit, NewTrackedEntityAttributeValueAudit } from './tracked-entity-attribute-value-audit.model';

export const sampleWithRequiredData: ITrackedEntityAttributeValueAudit = {
  id: 1479,
};

export const sampleWithPartialData: ITrackedEntityAttributeValueAudit = {
  id: 47674,
  encryptedValue: 'India',
  value: 'copying solutions',
  created: dayjs('2022-11-19T23:17'),
};

export const sampleWithFullData: ITrackedEntityAttributeValueAudit = {
  id: 42339,
  encryptedValue: 'Hat harness',
  plainValue: 'relationships',
  value: 'contingency bluetooth Central',
  modifiedBy: 'robust Corporate XML',
  created: dayjs('2022-11-20T06:32'),
  updated: dayjs('2022-11-20T19:54'),
  auditType: AuditType['UPDATE'],
};

export const sampleWithNewData: NewTrackedEntityAttributeValueAudit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
