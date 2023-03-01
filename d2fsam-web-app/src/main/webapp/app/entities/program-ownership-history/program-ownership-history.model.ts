import dayjs from 'dayjs/esm';
import { IProgram } from 'app/entities/program/program.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';

export interface IProgramOwnershipHistory {
  id: number;
  createdBy?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
}

export type NewProgramOwnershipHistory = Omit<IProgramOwnershipHistory, 'id'> & { id: null };
