import dayjs from 'dayjs/esm';
import { IPeriod } from 'app/entities/period/period.model';

export interface IDataInputPeriod {
  id: number;
  openingDate?: dayjs.Dayjs | null;
  closingDate?: dayjs.Dayjs | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
}

export type NewDataInputPeriod = Omit<IDataInputPeriod, 'id'> & { id: null };
