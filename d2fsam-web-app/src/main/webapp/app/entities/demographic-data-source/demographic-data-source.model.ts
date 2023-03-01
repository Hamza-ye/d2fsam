import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IDemographicDataSource {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDemographicDataSource = Omit<IDemographicDataSource, 'id'> & { id: null };
