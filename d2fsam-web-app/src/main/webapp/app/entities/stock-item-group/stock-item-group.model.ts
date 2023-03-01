import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IStockItem } from 'app/entities/stock-item/stock-item.model';

export interface IStockItemGroup {
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
  items?: Pick<IStockItem, 'id' | 'name'>[] | null;
}

export type NewStockItemGroup = Omit<IStockItemGroup, 'id'> & { id: null };
