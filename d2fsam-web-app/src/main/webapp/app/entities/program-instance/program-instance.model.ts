import dayjs from 'dayjs/esm';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { IProgram } from 'app/entities/program/program.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IActivity } from 'app/entities/activity/activity.model';
import { IUser } from 'app/entities/user/user.model';
import { IComment } from 'app/entities/comment/comment.model';
import { PeriodLabel } from 'app/entities/enumerations/period-label.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

export interface IProgramInstance {
  id: number;
  uid?: string | null;
  uuid?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  createdAtClient?: dayjs.Dayjs | null;
  updatedAtClient?: dayjs.Dayjs | null;
  lastSynchronized?: dayjs.Dayjs | null;
  incidentDate?: dayjs.Dayjs | null;
  enrollmentDate?: dayjs.Dayjs | null;
  periodLabel?: PeriodLabel | null;
  endDate?: dayjs.Dayjs | null;
  status?: EventStatus | null;
  storedBy?: string | null;
  completedBy?: string | null;
  completedDate?: dayjs.Dayjs | null;
  followup?: boolean | null;
  deleted?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  activity?: Pick<IActivity, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  approvedBy?: Pick<IUser, 'id' | 'login'> | null;
  comments?: Pick<IComment, 'id' | 'commentText'>[] | null;
}

export type NewProgramInstance = Omit<IProgramInstance, 'id'> & { id: null };
