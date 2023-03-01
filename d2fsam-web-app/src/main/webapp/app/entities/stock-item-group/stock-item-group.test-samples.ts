import dayjs from 'dayjs/esm';

import { IStockItemGroup, NewStockItemGroup } from './stock-item-group.model';

export const sampleWithRequiredData: IStockItemGroup = {
  id: 98122,
  name: 'Small',
};

export const sampleWithPartialData: IStockItemGroup = {
  id: 79134,
  name: 'Factors',
  shortName: 'methodical Administrator Bedfordshire',
  description: 'Marketing Liberia',
  created: dayjs('2022-11-20T20:19'),
  inactive: false,
};

export const sampleWithFullData: IStockItemGroup = {
  id: 53951,
  uid: 'Unbranded D',
  code: 'Cheese Berkshire Salad',
  name: 'Baby Refined',
  shortName: 'Creative',
  description: 'bandwidth',
  created: dayjs('2022-11-19T20:54'),
  updated: dayjs('2022-11-20T06:49'),
  inactive: false,
};

export const sampleWithNewData: NewStockItemGroup = {
  name: 'Clothing',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
