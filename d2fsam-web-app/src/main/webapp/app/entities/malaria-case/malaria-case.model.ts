import dayjs from 'dayjs/esm';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IUser } from 'app/entities/user/user.model';
import { Gender } from 'app/entities/enumerations/gender.model';
import { MalariaTestResult } from 'app/entities/enumerations/malaria-test-result.model';
import { Severity } from 'app/entities/enumerations/severity.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

export interface IMalariaCase {
  id: number;
  uuid?: string | null;
  code?: string | null;
  name?: string | null;
  entryStarted?: dayjs.Dayjs | null;
  lastSynced?: dayjs.Dayjs | null;
  deleted?: boolean | null;
  dateOfExamination?: dayjs.Dayjs | null;
  mobile?: string | null;
  gender?: Gender | null;
  age?: number | null;
  isPregnant?: boolean | null;
  malariaTestResult?: MalariaTestResult | null;
  severity?: Severity | null;
  referral?: boolean | null;
  barImageUrl?: string | null;
  comment?: string | null;
  status?: EventStatus | null;
  seen?: boolean | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  createdAtClient?: dayjs.Dayjs | null;
  updatedAtClient?: dayjs.Dayjs | null;
  deletedAt?: dayjs.Dayjs | null;
  subVillage?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewMalariaCase = Omit<IMalariaCase, 'id'> & { id: null };
