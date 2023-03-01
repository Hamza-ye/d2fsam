import dayjs from 'dayjs/esm';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

export interface ITrackedEntityInstanceAudit {
  id: number;
  trackedEntityInstance?: string | null;
  comment?: string | null;
  created?: dayjs.Dayjs | null;
  accessedBy?: string | null;
  auditType?: AuditType | null;
}

export type NewTrackedEntityInstanceAudit = Omit<ITrackedEntityInstanceAudit, 'id'> & { id: null };
