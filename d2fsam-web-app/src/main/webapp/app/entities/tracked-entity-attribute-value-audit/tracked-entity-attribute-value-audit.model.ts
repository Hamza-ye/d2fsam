import dayjs from 'dayjs/esm';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

export interface ITrackedEntityAttributeValueAudit {
  id: number;
  encryptedValue?: string | null;
  plainValue?: string | null;
  value?: string | null;
  modifiedBy?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  auditType?: AuditType | null;
  attribute?: Pick<IDataElement, 'id' | 'name'> | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
}

export type NewTrackedEntityAttributeValueAudit = Omit<ITrackedEntityAttributeValueAudit, 'id'> & { id: null };
