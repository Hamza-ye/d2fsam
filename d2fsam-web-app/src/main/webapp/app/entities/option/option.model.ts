import dayjs from 'dayjs/esm';
import { IOptionSet } from 'app/entities/option-set/option-set.model';
import { IUser } from 'app/entities/user/user.model';

export interface IOption {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  description?: string | null;
  sortOrder?: number | null;
  optionSet?: Pick<IOptionSet, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewOption = Omit<IOption, 'id'> & { id: null };
