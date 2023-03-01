import dayjs from 'dayjs/esm';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { IPeriod } from 'app/entities/period/period.model';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

export interface ITrackedEntityDataValueAudit {
  id: number;
  value?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  providedElsewhere?: boolean | null;
  auditType?: AuditType | null;
  programStageInstance?: Pick<IProgramStageInstance, 'id' | 'uid'> | null;
  dataElement?: Pick<IDataElement, 'id' | 'name'> | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
}

export type NewTrackedEntityDataValueAudit = Omit<ITrackedEntityDataValueAudit, 'id'> & { id: null };
