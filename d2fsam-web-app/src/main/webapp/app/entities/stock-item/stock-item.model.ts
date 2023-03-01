import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IStockItemGroup } from 'app/entities/stock-item-group/stock-item-group.model';

export interface IStockItem {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  shortName?: string | null;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  inactive?: boolean | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  groups?: Pick<IStockItemGroup, 'id'>[] | null;
}

export type NewStockItem = Omit<IStockItem, 'id'> & { id: null };
