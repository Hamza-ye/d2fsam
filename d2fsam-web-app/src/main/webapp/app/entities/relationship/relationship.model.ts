import dayjs from 'dayjs/esm';
import { IRelationshipType } from 'app/entities/relationship-type/relationship-type.model';
import { IRelationshipItem } from 'app/entities/relationship-item/relationship-item.model';
import { IUser } from 'app/entities/user/user.model';

export interface IRelationship {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  formName?: string | null;
  description?: string | null;
  key?: string | null;
  invertedKey?: string | null;
  deleted?: boolean | null;
  relationshipType?: Pick<IRelationshipType, 'id' | 'name'> | null;
  from?: Pick<IRelationshipItem, 'id'> | null;
  to?: Pick<IRelationshipItem, 'id'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewRelationship = Omit<IRelationship, 'id'> & { id: null };
