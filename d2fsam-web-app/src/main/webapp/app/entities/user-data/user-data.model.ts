import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IUserAuthorityGroup } from 'app/entities/user-authority-group/user-authority-group.model';
import { IUserGroup } from 'app/entities/user-group/user-group.model';
import { ITeam } from 'app/entities/team/team.model';

export interface IUserData {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  uuid?: string | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  organisationUnits?: Pick<IOrganisationUnit, 'id' | 'name'>[] | null;
  teiSearchOrganisationUnits?: Pick<IOrganisationUnit, 'id' | 'name'>[] | null;
  dataViewOrganisationUnits?: Pick<IOrganisationUnit, 'id' | 'name'>[] | null;
  userAuthorityGroups?: Pick<IUserAuthorityGroup, 'id' | 'name'>[] | null;
  groups?: Pick<IUserGroup, 'id'>[] | null;
  teams?: Pick<ITeam, 'id'>[] | null;
}

export type NewUserData = Omit<IUserData, 'id'> & { id: null };
