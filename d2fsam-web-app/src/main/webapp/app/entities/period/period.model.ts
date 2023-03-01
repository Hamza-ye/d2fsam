import dayjs from 'dayjs/esm';
import { IPeriodType } from 'app/entities/period-type/period-type.model';

export interface IPeriod {
  id: number;
  name?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  periodType?: Pick<IPeriodType, 'id'> | null;
}

export type NewPeriod = Omit<IPeriod, 'id'> & { id: null };
