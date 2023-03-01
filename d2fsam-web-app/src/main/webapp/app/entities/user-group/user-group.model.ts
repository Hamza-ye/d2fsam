import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IUserData } from 'app/entities/user-data/user-data.model';

export interface IUserGroup {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  uuid?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  activeFrom?: dayjs.Dayjs | null;
  activeTo?: dayjs.Dayjs | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  members?: Pick<IUserData, 'id' | 'name'>[] | null;
  managedGroups?: Pick<IUserGroup, 'id' | 'name'>[] | null;
  managedByGroups?: Pick<IUserGroup, 'id'>[] | null;
}

export type NewUserGroup = Omit<IUserGroup, 'id'> & { id: null };
