import dayjs from 'dayjs/esm';

import { IStockItem, NewStockItem } from './stock-item.model';

export const sampleWithRequiredData: IStockItem = {
  id: 89819,
  name: 'Armenia',
};

export const sampleWithPartialData: IStockItem = {
  id: 55284,
  uid: 'up',
  code: 'Practical whiteboard',
  name: 'open-source Buckinghamshire models',
  description: 'Wooden info-mediaries',
  created: dayjs('2022-11-20T14:52'),
  updated: dayjs('2022-11-20T09:22'),
};

export const sampleWithFullData: IStockItem = {
  id: 68420,
  uid: 'Communicati',
  code: 'indexing',
  name: 'database bluetooth',
  shortName: 'Home navigating',
  description: 'synthesize',
  created: dayjs('2022-11-20T06:47'),
  updated: dayjs('2022-11-20T09:22'),
  inactive: false,
};

export const sampleWithNewData: NewStockItem = {
  name: 'Platinum',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
