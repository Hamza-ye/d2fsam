import dayjs from 'dayjs/esm';

import { ValidationStrategy } from 'app/entities/enumerations/validation-strategy.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

import { IProgramStage, NewProgramStage } from './program-stage.model';

export const sampleWithRequiredData: IProgramStage = {
  id: 12484,
  uid: 'Kentucky Di',
  name: 'Fantastic Tasty',
};

export const sampleWithPartialData: IProgramStage = {
  id: 38688,
  uid: 'Hat',
  created: dayjs('2022-11-19T23:26'),
  updated: dayjs('2022-11-20T11:09'),
  name: 'PNG Developer feed',
  description: 'transitional',
  minDaysFromStart: 67866,
  autoGenerateEvent: true,
  blockEntryForm: false,
  sortOrder: 36335,
  enableTeamAssignment: false,
  inactive: true,
};

export const sampleWithFullData: IProgramStage = {
  id: 83108,
  uid: 'client-serv',
  code: 'Square navigate',
  created: dayjs('2022-11-20T03:00'),
  updated: dayjs('2022-11-20T06:06'),
  name: 'capacity Metal',
  description: 'engineer Principal',
  minDaysFromStart: 13904,
  repeatable: true,
  executionDateLabel: 'Refined Dollar',
  dueDateLabel: 'National access Refined',
  autoGenerateEvent: true,
  validationStrategy: ValidationStrategy['ON_COMPLETE'],
  blockEntryForm: true,
  openAfterEnrollment: true,
  generatedByEnrollmentDate: true,
  sortOrder: 94773,
  hideDueDate: false,
  featureType: FeatureType['POINT'],
  enableUserAssignment: false,
  enableTeamAssignment: true,
  inactive: true,
};

export const sampleWithNewData: NewProgramStage = {
  uid: 'Table Loan ',
  name: 'Fields',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
