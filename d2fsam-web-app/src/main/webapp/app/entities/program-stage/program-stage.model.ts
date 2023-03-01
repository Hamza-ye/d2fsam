import dayjs from 'dayjs/esm';
import { IPeriod } from 'app/entities/period/period.model';
import { IProgram } from 'app/entities/program/program.model';
import { IUser } from 'app/entities/user/user.model';
import { ValidationStrategy } from 'app/entities/enumerations/validation-strategy.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

export interface IProgramStage {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  name?: string | null;
  description?: string | null;
  minDaysFromStart?: number | null;
  repeatable?: boolean | null;
  executionDateLabel?: string | null;
  dueDateLabel?: string | null;
  autoGenerateEvent?: boolean | null;
  validationStrategy?: ValidationStrategy | null;
  blockEntryForm?: boolean | null;
  openAfterEnrollment?: boolean | null;
  generatedByEnrollmentDate?: boolean | null;
  sortOrder?: number | null;
  hideDueDate?: boolean | null;
  featureType?: FeatureType | null;
  enableUserAssignment?: boolean | null;
  enableTeamAssignment?: boolean | null;
  inactive?: boolean | null;
  periodType?: Pick<IPeriod, 'id'> | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProgramStage = Omit<IProgramStage, 'id'> & { id: null };
