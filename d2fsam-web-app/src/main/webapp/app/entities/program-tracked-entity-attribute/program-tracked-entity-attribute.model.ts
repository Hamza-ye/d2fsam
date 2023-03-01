import dayjs from 'dayjs/esm';
import { IProgram } from 'app/entities/program/program.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { IUser } from 'app/entities/user/user.model';

export interface IProgramTrackedEntityAttribute {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  displayInList?: boolean | null;
  sortOrder?: number | null;
  mandatory?: boolean | null;
  allowFutureDate?: boolean | null;
  searchable?: boolean | null;
  defaultValue?: string | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  attribute?: Pick<IDataElement, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProgramTrackedEntityAttribute = Omit<IProgramTrackedEntityAttribute, 'id'> & { id: null };
