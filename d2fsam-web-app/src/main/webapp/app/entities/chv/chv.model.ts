import dayjs from 'dayjs/esm';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IUser } from 'app/entities/user/user.model';

export interface IChv {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  withdrawn?: boolean | null;
  dateJoined?: dayjs.Dayjs | null;
  dateWithdrawn?: dayjs.Dayjs | null;
  name?: string | null;
  description?: string | null;
  inactive?: boolean | null;
  assignedTo?: Pick<IUserData, 'id' | 'name'> | null;
  district?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  homeSubvillage?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  managingHf?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewChv = Omit<IChv, 'id'> & { id: null };
