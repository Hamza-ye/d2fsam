import dayjs from 'dayjs/esm';
import { IPeriod } from 'app/entities/period/period.model';
import { IPeriodType } from 'app/entities/period-type/period-type.model';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { IUser } from 'app/entities/user/user.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { ProgramType } from 'app/entities/enumerations/program-type.model';
import { AccessLevel } from 'app/entities/enumerations/access-level.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

export interface IProgram {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  name?: string | null;
  shortName?: string | null;
  description?: string | null;
  version?: number | null;
  incidentDateLabel?: string | null;
  programType?: ProgramType | null;
  displayIncidentDate?: boolean | null;
  onlyEnrollOnce?: boolean | null;
  captureCoordinates?: boolean | null;
  expiryDays?: number | null;
  completeEventsExpiryDays?: number | null;
  accessLevel?: AccessLevel | null;
  ignoreOverDueEvents?: boolean | null;
  selectEnrollmentDatesInFuture?: boolean | null;
  selectIncidentDatesInFuture?: boolean | null;
  featureType?: FeatureType | null;
  inactive?: boolean | null;
  period?: Pick<IPeriod, 'id' | 'name'> | null;
  expiryPeriodType?: Pick<IPeriodType, 'id' | 'name'> | null;
  relatedProgram?: Pick<IProgram, 'id' | 'name'> | null;
  trackedEntityType?: Pick<ITrackedEntityType, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  organisationUnits?: Pick<IOrganisationUnit, 'id' | 'name'>[] | null;
}

export type NewProgram = Omit<IProgram, 'id'> & { id: null };
