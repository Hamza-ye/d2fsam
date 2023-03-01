import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { SqlViewType } from 'app/entities/enumerations/sql-view-type.model';
import { CacheStrategy } from 'app/entities/enumerations/cache-strategy.model';

export interface ISqlView {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  description?: string | null;
  sqlQuery?: string | null;
  type?: SqlViewType | null;
  cacheStrategy?: CacheStrategy | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewSqlView = Omit<ISqlView, 'id'> & { id: null };
