import dayjs from 'dayjs/esm';

import { AuditType } from 'app/entities/enumerations/audit-type.model';

import { ITrackedEntityInstanceAudit, NewTrackedEntityInstanceAudit } from './tracked-entity-instance-audit.model';

export const sampleWithRequiredData: ITrackedEntityInstanceAudit = {
  id: 66944,
};

export const sampleWithPartialData: ITrackedEntityInstanceAudit = {
  id: 66130,
  trackedEntityInstance: '4th',
  comment: 'Hat Cambridgeshire',
  accessedBy: 'Senior',
  auditType: AuditType['UPDATE'],
};

export const sampleWithFullData: ITrackedEntityInstanceAudit = {
  id: 71270,
  trackedEntityInstance: 'RAM generate',
  comment: 'Cote solid Ball',
  created: dayjs('2022-11-20T01:25'),
  accessedBy: 'Senior Kroon',
  auditType: AuditType['READ'],
};

export const sampleWithNewData: NewTrackedEntityInstanceAudit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
