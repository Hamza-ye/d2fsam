import dayjs from 'dayjs/esm';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { IUser } from 'app/entities/user/user.model';
import { IDemographicDataSource } from 'app/entities/demographic-data-source/demographic-data-source.model';
import { DemographicDataLevel } from 'app/entities/enumerations/demographic-data-level.model';

export interface IDemographicData {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  date?: dayjs.Dayjs | null;
  level?: DemographicDataLevel | null;
  totalPopulation?: number | null;
  malePopulation?: number | null;
  femalePopulation?: number | null;
  lessThan5Population?: number | null;
  greaterThan5Population?: number | null;
  bw5And15Population?: number | null;
  greaterThan15Population?: number | null;
  households?: number | null;
  houses?: number | null;
  healthFacilities?: number | null;
  avgNoOfRooms?: number | null;
  avgRoomArea?: number | null;
  avgHouseArea?: number | null;
  individualsPerHousehold?: number | null;
  populationGrowthRate?: number | null;
  comment?: string | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
  source?: Pick<IDemographicDataSource, 'id' | 'name'> | null;
}

export type NewDemographicData = Omit<IDemographicData, 'id'> & { id: null };
