import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IDatastoreEntry {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  key?: string | null;
  namespace?: string | null;
  encrypted?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDatastoreEntry = Omit<IDatastoreEntry, 'id'> & { id: null };
