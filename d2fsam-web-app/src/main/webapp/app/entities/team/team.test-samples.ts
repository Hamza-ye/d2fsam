import dayjs from 'dayjs/esm';

import { TeamType } from 'app/entities/enumerations/team-type.model';

import { ITeam, NewTeam } from './team.model';

export const sampleWithRequiredData: ITeam = {
  id: 88024,
  uid: 'Chief progr',
  teamType: TeamType['OTHER'],
};

export const sampleWithPartialData: ITeam = {
  id: 78743,
  uid: 'Automotive ',
  code: 'deposit',
  created: dayjs('2022-11-19T21:11'),
  name: 'Grocery Berkshire',
  comments: 'Maine experiences',
  rating: 87,
  teamType: TeamType['IRS_TEAM'],
  inactive: true,
};

export const sampleWithFullData: ITeam = {
  id: 33594,
  uid: 'navigating ',
  code: 'Account drive',
  created: dayjs('2022-11-20T07:26'),
  updated: dayjs('2022-11-20T04:28'),
  name: 'JSON implement',
  description: 'Digitized Licensed',
  comments: 'Administrator',
  rating: 31,
  teamType: TeamType['CHV_SUPERVISOR'],
  inactive: true,
};

export const sampleWithNewData: NewTeam = {
  uid: 'Wooden calc',
  teamType: TeamType['OR'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
