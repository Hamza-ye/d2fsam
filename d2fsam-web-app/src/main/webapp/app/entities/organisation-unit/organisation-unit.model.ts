import dayjs from 'dayjs/esm';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { IUser } from 'app/entities/user/user.model';
import { IChv } from 'app/entities/chv/chv.model';
import { IProgram } from 'app/entities/program/program.model';
import { IActivity } from 'app/entities/activity/activity.model';
import { IOrganisationUnitGroup } from 'app/entities/organisation-unit-group/organisation-unit-group.model';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { OrganisationUnitType } from 'app/entities/enumerations/organisation-unit-type.model';

export interface IOrganisationUnit {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  shortName?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  path?: string | null;
  hierarchyLevel?: number | null;
  openingDate?: dayjs.Dayjs | null;
  comment?: string | null;
  closedDate?: dayjs.Dayjs | null;
  url?: string | null;
  contactPerson?: string | null;
  address?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  organisationUnitType?: OrganisationUnitType | null;
  inactive?: boolean | null;
  parent?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  hfHomeSubVillage?: Pick<IOrganisationUnit, 'id'> | null;
  servicingHf?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  image?: Pick<IFileResource, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  assignedChv?: Pick<IChv, 'id' | 'code'> | null;
  programs?: Pick<IProgram, 'id'>[] | null;
  targetedInActivities?: Pick<IActivity, 'id' | 'name'>[] | null;
  groups?: Pick<IOrganisationUnitGroup, 'id'>[] | null;
  users?: Pick<IUserData, 'id'>[] | null;
  searchingUsers?: Pick<IUserData, 'id'>[] | null;
  dataViewUsers?: Pick<IUserData, 'id'>[] | null;
}

export type NewOrganisationUnit = Omit<IOrganisationUnit, 'id'> & { id: null };
