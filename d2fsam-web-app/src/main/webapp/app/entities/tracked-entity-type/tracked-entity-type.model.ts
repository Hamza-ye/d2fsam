import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

export interface ITrackedEntityType {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  name?: string | null;
  description?: string | null;
  maxTeiCountToReturn?: number | null;
  allowAuditLog?: boolean | null;
  featureType?: FeatureType | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTrackedEntityType = Omit<ITrackedEntityType, 'id'> & { id: null };
