import dayjs from 'dayjs/esm';

import { ITeamGroup, NewTeamGroup } from './team-group.model';

export const sampleWithRequiredData: ITeamGroup = {
  id: 89303,
  uid: 'optical Int',
  name: 'aggregate',
};

export const sampleWithPartialData: ITeamGroup = {
  id: 4002,
  uid: 'Refined fee',
  name: 'Incredible hacking Kids',
  created: dayjs('2022-11-20T14:45'),
  activeFrom: dayjs('2022-11-20T15:57'),
  activeTo: dayjs('2022-11-20T18:59'),
  inactive: false,
};

export const sampleWithFullData: ITeamGroup = {
  id: 6015,
  uid: 'circuit',
  code: 'Supervisor portals',
  name: 'Bedfordshire Bacon',
  uuid: '9725cada-70cf-412a-a46a-477a34e3efe7',
  created: dayjs('2022-11-20T03:16'),
  updated: dayjs('2022-11-20T13:31'),
  activeFrom: dayjs('2022-11-19T20:53'),
  activeTo: dayjs('2022-11-19T21:23'),
  inactive: true,
};

export const sampleWithNewData: NewTeamGroup = {
  uid: 'bypass',
  name: 'generating executive Industrial',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
