import dayjs from 'dayjs/esm';

import { IDataInputPeriod, NewDataInputPeriod } from './data-input-period.model';

export const sampleWithRequiredData: IDataInputPeriod = {
  id: 32842,
};

export const sampleWithPartialData: IDataInputPeriod = {
  id: 59473,
};

export const sampleWithFullData: IDataInputPeriod = {
  id: 45621,
  openingDate: dayjs('2022-11-20'),
  closingDate: dayjs('2022-11-20'),
};

export const sampleWithNewData: NewDataInputPeriod = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
