import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { FileResourceDomain } from 'app/entities/enumerations/file-resource-domain.model';

export interface IFileResource {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  contentType?: string | null;
  contentLength?: number | null;
  contentMd5?: string | null;
  storageKey?: string | null;
  assigned?: boolean | null;
  domain?: FileResourceDomain | null;
  hasMultipleStorageFiles?: boolean | null;
  fileResourceOwner?: string | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewFileResource = Omit<IFileResource, 'id'> & { id: null };
