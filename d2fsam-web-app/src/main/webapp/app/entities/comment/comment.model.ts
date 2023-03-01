import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';

export interface IComment {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  commentText?: string | null;
  creator?: string | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  programInstances?: Pick<IProgramInstance, 'id'>[] | null;
  programStageInstances?: Pick<IProgramStageInstance, 'id'>[] | null;
}

export type NewComment = Omit<IComment, 'id'> & { id: null };
