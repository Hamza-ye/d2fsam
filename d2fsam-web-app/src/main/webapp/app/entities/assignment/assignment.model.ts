import dayjs from 'dayjs/esm';
import { IActivity } from 'app/entities/activity/activity.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { ITeam } from 'app/entities/team/team.model';
import { IPeriod } from 'app/entities/period/period.model';
import { IUser } from 'app/entities/user/user.model';
import { PeriodLabel } from 'app/entities/enumerations/period-label.model';
import { TargetSource } from 'app/entities/enumerations/target-source.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

export interface IAssignment {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  description?: string | null;
  startDate?: dayjs.Dayjs | null;
  startPeriod?: PeriodLabel | null;
  targetSource?: TargetSource | null;
  status?: EventStatus | null;
  deleted?: boolean | null;
  deletedAt?: dayjs.Dayjs | null;
  activity?: Pick<IActivity, 'id' | 'name'> | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  assignedTeam?: Pick<ITeam, 'id' | 'name'> | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewAssignment = Omit<IAssignment, 'id'> & { id: null };
