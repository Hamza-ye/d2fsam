import dayjs from 'dayjs/esm';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { IUser } from 'app/entities/user/user.model';

export interface IDocument {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  url?: string | null;
  external?: boolean | null;
  contentType?: string | null;
  attachment?: boolean | null;
  fileResource?: Pick<IFileResource, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDocument = Omit<IDocument, 'id'> & { id: null };
