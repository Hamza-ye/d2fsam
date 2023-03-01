import dayjs from 'dayjs/esm';

import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 55962,
  uid: 'COM dedicat',
  name: 'Awesome users',
};

export const sampleWithPartialData: IProject = {
  id: 50573,
  uid: 'Avon Ball',
  code: 'Loan bluetooth Tasty',
  name: 'indexing',
  shortName: 'Niger (Slovak mindshare',
  description: 'Bike Timor-Leste',
  created: dayjs('2022-11-20T00:53'),
};

export const sampleWithFullData: IProject = {
  id: 2133,
  uid: 'Proactive',
  code: 'productize Data',
  name: 'USB capability',
  shortName: 'Canyon syndicate',
  description: 'fresh-thinking Granite EXE',
  created: dayjs('2022-11-20T10:35'),
  updated: dayjs('2022-11-20T13:51'),
  hidden: true,
  order: 46732,
  inactive: true,
};

export const sampleWithNewData: NewProject = {
  uid: 'Administrat',
  name: 'auxiliary',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
