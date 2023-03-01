import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IProject {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  shortName?: string | null;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  hidden?: boolean | null;
  order?: number | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProject = Omit<IProject, 'id'> & { id: null };
