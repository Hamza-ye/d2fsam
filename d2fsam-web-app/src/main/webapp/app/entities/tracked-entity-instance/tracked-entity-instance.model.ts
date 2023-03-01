import dayjs from 'dayjs/esm';
import { IPeriod } from 'app/entities/period/period.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { IUser } from 'app/entities/user/user.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

export interface ITrackedEntityInstance {
  id: number;
  uid?: string | null;
  uuid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  createdAtClient?: dayjs.Dayjs | null;
  updatedAtClient?: dayjs.Dayjs | null;
  lastSynchronized?: dayjs.Dayjs | null;
  featureType?: FeatureType | null;
  coordinates?: string | null;
  potentialDuplicate?: boolean | null;
  deleted?: boolean | null;
  storedBy?: string | null;
  inactive?: boolean | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  trackedEntityType?: Pick<ITrackedEntityType, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTrackedEntityInstance = Omit<ITrackedEntityInstance, 'id'> & { id: null };
