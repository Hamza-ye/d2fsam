import dayjs from 'dayjs/esm';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { IProgram } from 'app/entities/program/program.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';

export interface ITrackedEntityProgramOwner {
  id: number;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  createdBy?: string | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
  program?: Pick<IProgram, 'id' | 'uid'> | null;
  organisationUnit?: Pick<IOrganisationUnit, 'id' | 'name'> | null;
}

export type NewTrackedEntityProgramOwner = Omit<ITrackedEntityProgramOwner, 'id'> & { id: null };
