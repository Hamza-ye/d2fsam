import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IOrganisationUnitGroup } from 'app/entities/organisation-unit-group/organisation-unit-group.model';

export interface IOrganisationUnitGroupSet {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  compulsory?: boolean | null;
  includeSubhierarchyInAnalytics?: boolean | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  organisationUnitGroups?: Pick<IOrganisationUnitGroup, 'id' | 'name'>[] | null;
}

export type NewOrganisationUnitGroupSet = Omit<IOrganisationUnitGroupSet, 'id'> & { id: null };
