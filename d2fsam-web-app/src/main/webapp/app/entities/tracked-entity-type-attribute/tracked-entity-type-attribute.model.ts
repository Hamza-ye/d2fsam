import dayjs from 'dayjs/esm';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { IUser } from 'app/entities/user/user.model';

export interface ITrackedEntityTypeAttribute {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  displayInList?: boolean | null;
  mandatory?: boolean | null;
  searchable?: boolean | null;
  trackedEntityAttribute?: Pick<IDataElement, 'id' | 'name'> | null;
  trackedEntityType?: Pick<ITrackedEntityType, 'id' | 'name'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTrackedEntityTypeAttribute = Omit<ITrackedEntityTypeAttribute, 'id'> & { id: null };
