import dayjs from 'dayjs/esm';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { IPeriod } from 'app/entities/period/period.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { AuditType } from 'app/entities/enumerations/audit-type.model';

export interface IDataValue {
  id: number;
  value?: string | null;
  storedBy?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  comment?: string | null;
  followup?: boolean | null;
  deleted?: boolean | null;
  auditType?: AuditType | null;
  dataElement?: Pick<IDataElement, 'id' | 'name'> | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
  source?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
}

export type NewDataValue = Omit<IDataValue, 'id'> & { id: null };
