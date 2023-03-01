import dayjs from 'dayjs/esm';

import { IOrganisationUnitGroup, NewOrganisationUnitGroup } from './organisation-unit-group.model';

export const sampleWithRequiredData: IOrganisationUnitGroup = {
  id: 42659,
  uid: 'Bike info-m',
  name: 'compressing transmit connecting',
};

export const sampleWithPartialData: IOrganisationUnitGroup = {
  id: 58655,
  uid: 'dot-com',
  name: 'haptic Cambridgeshire sky',
  shortName: 'Practical eyeballs',
  created: dayjs('2022-11-19T21:27'),
  color: 'silver',
};

export const sampleWithFullData: IOrganisationUnitGroup = {
  id: 75022,
  uid: 'Incredible ',
  code: 'to copy',
  name: 'Hat XML',
  shortName: 'generate Gorgeous',
  created: dayjs('2022-11-20T02:40'),
  updated: dayjs('2022-11-20T05:07'),
  symbol: 'Borders',
  color: 'pink',
  inactive: false,
};

export const sampleWithNewData: NewOrganisationUnitGroup = {
  uid: 'Computer cr',
  name: 'azure quantifying',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
