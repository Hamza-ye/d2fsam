import dayjs from 'dayjs/esm';
import { ITeam } from 'app/entities/team/team.model';

export interface ITeamGroup {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  uuid?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  activeFrom?: dayjs.Dayjs | null;
  activeTo?: dayjs.Dayjs | null;
  inactive?: boolean | null;
  members?: Pick<ITeam, 'id' | 'name'>[] | null;
}

export type NewTeamGroup = Omit<ITeamGroup, 'id'> & { id: null };
