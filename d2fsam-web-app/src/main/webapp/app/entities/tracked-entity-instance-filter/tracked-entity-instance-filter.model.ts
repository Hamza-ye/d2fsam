import dayjs from 'dayjs/esm';
import { IProgram } from 'app/entities/program/program.model';
import { IUser } from 'app/entities/user/user.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

export interface ITrackedEntityInstanceFilter {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  description?: string | null;
  sortOrder?: number | null;
  enrollmentStatus?: EventStatus | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTrackedEntityInstanceFilter = Omit<ITrackedEntityInstanceFilter, 'id'> & { id: null };
