import dayjs from 'dayjs/esm';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IUser } from 'app/entities/user/user.model';
import { IPeriod } from 'app/entities/period/period.model';
import { IComment } from 'app/entities/comment/comment.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

export interface IProgramStageInstance {
  id: number;
  uid?: string | null;
  uuid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  createdAtClient?: dayjs.Dayjs | null;
  updatedAtClient?: dayjs.Dayjs | null;
  lastSynchronized?: dayjs.Dayjs | null;
  dueDate?: dayjs.Dayjs | null;
  executionDate?: dayjs.Dayjs | null;
  status?: EventStatus | null;
  storedBy?: string | null;
  completedBy?: string | null;
  completedDate?: dayjs.Dayjs | null;
  deleted?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  programInstance?: Pick<IProgramInstance, 'id' | 'uid'> | null;
  programStage?: Pick<IProgramStage, 'id' | 'name'> | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  assignedUser?: Pick<IUser, 'id' | 'login'> | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  approvedBy?: Pick<IUser, 'id' | 'login'> | null;
  comments?: Pick<IComment, 'id' | 'commentText'>[] | null;
}

export type NewProgramStageInstance = Omit<IProgramStageInstance, 'id'> & { id: null };
