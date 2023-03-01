import dayjs from 'dayjs/esm';

export interface IOrganisationUnitLevel {
  id: number;
  uid?: string | null;
  code?: string | null;
  name?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  level?: number | null;
  offlineLevels?: number | null;
}

export type NewOrganisationUnitLevel = Omit<IOrganisationUnitLevel, 'id'> & { id: null };
