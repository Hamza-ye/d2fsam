import dayjs from 'dayjs/esm';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { IUser } from 'app/entities/user/user.model';

export interface IExternalFileResource {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  accessToken?: string | null;
  expires?: dayjs.Dayjs | null;
  fileResource?: Pick<IFileResource, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewExternalFileResource = Omit<IExternalFileResource, 'id'> & { id: null };
