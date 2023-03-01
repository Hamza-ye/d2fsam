import dayjs from 'dayjs/esm';

import { Gender } from 'app/entities/enumerations/gender.model';
import { MalariaTestResult } from 'app/entities/enumerations/malaria-test-result.model';
import { Severity } from 'app/entities/enumerations/severity.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

import { IMalariaCase, NewMalariaCase } from './malaria-case.model';

export const sampleWithRequiredData: IMalariaCase = {
  id: 56723,
};

export const sampleWithPartialData: IMalariaCase = {
  id: 87150,
  code: 'Account',
  name: 'cross-platform',
  entryStarted: dayjs('2022-11-19T21:15'),
  deleted: true,
  gender: Gender['FEMALE'],
  age: 16752,
  severity: Severity['SIMPLE'],
  barImageUrl: 'Hat Home Gloves',
  comment: 'Data',
  status: EventStatus['ACTIVE'],
  created: dayjs('2022-11-20T19:15'),
  updated: dayjs('2022-11-20T11:30'),
  createdAtClient: dayjs('2022-11-20T18:31'),
  updatedAtClient: dayjs('2022-11-20T15:47'),
  deletedAt: dayjs('2022-11-20T16:49'),
};

export const sampleWithFullData: IMalariaCase = {
  id: 51118,
  uuid: '46893e95-85f1-4463-b6ce-ad9c3689fdc8',
  code: 'utilisation primary redefine',
  name: 'payment FTP PNG',
  entryStarted: dayjs('2022-11-19T22:38'),
  lastSynced: dayjs('2022-11-20T12:14'),
  deleted: false,
  dateOfExamination: dayjs('2022-11-19'),
  mobile: 'District withdrawal',
  gender: Gender['MALE'],
  age: 82744,
  isPregnant: true,
  malariaTestResult: MalariaTestResult['PV'],
  severity: Severity['SEVERE'],
  referral: true,
  barImageUrl: 'cross-platform generation back-end',
  comment: 'Silver Cotton',
  status: EventStatus['COMPLETED'],
  seen: false,
  created: dayjs('2022-11-20T06:35'),
  updated: dayjs('2022-11-20T10:54'),
  createdAtClient: dayjs('2022-11-20T16:22'),
  updatedAtClient: dayjs('2022-11-20T09:51'),
  deletedAt: dayjs('2022-11-20T16:10'),
};

export const sampleWithNewData: NewMalariaCase = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
