import dayjs from 'dayjs/esm';
import { IProject } from 'app/entities/project/project.model';
import { IDemographicData } from 'app/entities/demographic-data/demographic-data.model';
import { IUser } from 'app/entities/user/user.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';

export interface IActivity {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  shortName?: string | null;
  description?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  hidden?: boolean | null;
  order?: number | null;
  inactive?: boolean | null;
  project?: Pick<IProject, 'id' | 'name'> | null;
  activityUsedAsTarget?: Pick<IActivity, 'id' | 'name'> | null;
  demographicData?: Pick<IDemographicData, 'id'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  targetedOrganisationUnits?: Pick<IOrganisationUnit, 'id' | 'name'>[] | null;
}

export type NewActivity = Omit<IActivity, 'id'> & { id: null };
