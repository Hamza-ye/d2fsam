import dayjs from 'dayjs/esm';
import { IRelationshipConstraint } from 'app/entities/relationship-constraint/relationship-constraint.model';
import { IUser } from 'app/entities/user/user.model';

export interface IRelationshipType {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  description?: string | null;
  bidirectional?: boolean | null;
  fromToName?: string | null;
  toFromName?: string | null;
  referral?: boolean | null;
  fromConstraint?: Pick<IRelationshipConstraint, 'id'> | null;
  toConstraint?: Pick<IRelationshipConstraint, 'id'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewRelationshipType = Omit<IRelationshipType, 'id'> & { id: null };
