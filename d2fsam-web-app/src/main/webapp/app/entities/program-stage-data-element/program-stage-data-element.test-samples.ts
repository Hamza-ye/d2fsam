import dayjs from 'dayjs/esm';

import { IProgramStageDataElement, NewProgramStageDataElement } from './program-stage-data-element.model';

export const sampleWithRequiredData: IProgramStageDataElement = {
  id: 89483,
  uid: 'Dynamic Com',
};

export const sampleWithPartialData: IProgramStageDataElement = {
  id: 49015,
  uid: 'Legacy capa',
  updated: dayjs('2022-11-20T08:46'),
  compulsory: true,
  allowProvidedElsewhere: false,
  sortOrder: 47282,
  allowFutureDate: true,
};

export const sampleWithFullData: IProgramStageDataElement = {
  id: 17830,
  uid: 'solutions',
  code: 'Dynamic matrix index',
  created: dayjs('2022-11-20T13:02'),
  updated: dayjs('2022-11-20T05:12'),
  compulsory: true,
  allowProvidedElsewhere: true,
  sortOrder: 96552,
  displayInReports: false,
  allowFutureDate: false,
  skipSynchronization: true,
  defaultValue: 'Music Lead navigating',
};

export const sampleWithNewData: NewProgramStageDataElement = {
  uid: 'withdrawal',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
