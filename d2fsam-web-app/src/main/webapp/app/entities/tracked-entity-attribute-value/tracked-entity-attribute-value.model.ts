import dayjs from 'dayjs/esm';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';

export interface ITrackedEntityAttributeValue {
  id: number;
  encryptedValue?: string | null;
  plainValue?: string | null;
  value?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  storedBy?: string | null;
  attribute?: Pick<IDataElement, 'id' | 'name'> | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
}

export type NewTrackedEntityAttributeValue = Omit<ITrackedEntityAttributeValue, 'id'> & { id: null };
