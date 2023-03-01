import dayjs from 'dayjs/esm';

import { ProgramType } from 'app/entities/enumerations/program-type.model';
import { AccessLevel } from 'app/entities/enumerations/access-level.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

import { IProgram, NewProgram } from './program.model';

export const sampleWithRequiredData: IProgram = {
  id: 72488,
  uid: 'Maine SCSI ',
  name: 'iterate',
};

export const sampleWithPartialData: IProgram = {
  id: 41731,
  uid: 'Ruble',
  code: 'Squares Chair',
  created: dayjs('2022-11-20T12:08'),
  name: 'context-sensitive',
  description: 'Diverse Wooden',
  version: 40445,
  incidentDateLabel: 'fresh-thinking transmit',
  onlyEnrollOnce: false,
  expiryDays: 74633,
  selectEnrollmentDatesInFuture: true,
  featureType: FeatureType['POLYGON'],
};

export const sampleWithFullData: IProgram = {
  id: 46939,
  uid: 'indexing',
  code: 'Savings Sum',
  created: dayjs('2022-11-20T20:38'),
  updated: dayjs('2022-11-19T21:41'),
  name: 'Wooden Fall payment',
  shortName: 'experiences Syrian tolerance',
  description: 'Computers',
  version: 22443,
  incidentDateLabel: 'innovative Zloty',
  programType: ProgramType['WITHOUT_REGISTRATION'],
  displayIncidentDate: false,
  onlyEnrollOnce: true,
  captureCoordinates: false,
  expiryDays: 97532,
  completeEventsExpiryDays: 13304,
  accessLevel: AccessLevel['PROTECTED'],
  ignoreOverDueEvents: false,
  selectEnrollmentDatesInFuture: true,
  selectIncidentDatesInFuture: false,
  featureType: FeatureType['POLYGON'],
  inactive: false,
};

export const sampleWithNewData: NewProgram = {
  uid: 'Web Michiga',
  name: 'Kingdom withdrawal',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
