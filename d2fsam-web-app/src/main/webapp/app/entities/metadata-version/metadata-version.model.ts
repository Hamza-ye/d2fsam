import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { VersionType } from 'app/entities/enumerations/version-type.model';

export interface IMetadataVersion {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  importDate?: dayjs.Dayjs | null;
  type?: VersionType | null;
  hashCode?: string | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewMetadataVersion = Omit<IMetadataVersion, 'id'> & { id: null };
