import dayjs from 'dayjs/esm';
import { IProgram } from 'app/entities/program/program.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';

export interface IProgramTempOwnershipAudit {
  id: number;
  reason?: string | null;
  created?: dayjs.Dayjs | null;
  accessedBy?: string | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
}

export type NewProgramTempOwnershipAudit = Omit<IProgramTempOwnershipAudit, 'id'> & { id: null };
