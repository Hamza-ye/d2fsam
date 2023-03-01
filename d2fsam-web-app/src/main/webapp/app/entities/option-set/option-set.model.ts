import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ValueType } from 'app/entities/enumerations/value-type.model';

export interface IOptionSet {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  valueType?: ValueType | null;
  version?: number | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewOptionSet = Omit<IOptionSet, 'id'> & { id: null };
