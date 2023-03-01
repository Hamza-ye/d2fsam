import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IOrganisationUnitGroupSet } from 'app/entities/organisation-unit-group-set/organisation-unit-group-set.model';

export interface IOrganisationUnitGroup {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  shortName?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  symbol?: string | null;
  color?: string | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  members?: Pick<IOrganisationUnit, 'id' | 'name'>[] | null;
  groupSets?: Pick<IOrganisationUnitGroupSet, 'id'>[] | null;
}

export type NewOrganisationUnitGroup = Omit<IOrganisationUnitGroup, 'id'> & { id: null };
