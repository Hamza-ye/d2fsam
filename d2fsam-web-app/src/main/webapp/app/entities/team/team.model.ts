import dayjs from 'dayjs/esm';
import { IActivity } from 'app/entities/activity/activity.model';
import { IUser } from 'app/entities/user/user.model';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { ITeamGroup } from 'app/entities/team-group/team-group.model';
import { TeamType } from 'app/entities/enumerations/team-type.model';

export interface ITeam {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  name?: string | null;
  description?: string | null;
  comments?: string | null;
  rating?: number | null;
  teamType?: TeamType | null;
  inactive?: boolean | null;
  activity?: Pick<IActivity, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  members?: Pick<IUserData, 'id' | 'name'>[] | null;
  managedTeams?: Pick<ITeam, 'id' | 'name'>[] | null;
  groups?: Pick<ITeamGroup, 'id'>[] | null;
  managedByTeams?: Pick<ITeam, 'id'>[] | null;
}

export type NewTeam = Omit<ITeam, 'id'> & { id: null };
