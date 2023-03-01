import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IProgramStageInstanceFilter {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  description?: string | null;
  program?: string | null;
  programStage?: string | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProgramStageInstanceFilter = Omit<IProgramStageInstanceFilter, 'id'> & { id: null };
