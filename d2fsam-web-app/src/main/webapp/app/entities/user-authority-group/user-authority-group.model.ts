import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IUserData } from 'app/entities/user-data/user-data.model';

export interface IUserAuthorityGroup {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  members?: Pick<IUserData, 'id'>[] | null;
}

export type NewUserAuthorityGroup = Omit<IUserAuthorityGroup, 'id'> & { id: null };
