import dayjs from 'dayjs/esm';
import { IProgram } from 'app/entities/program/program.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { IUser } from 'app/entities/user/user.model';

export interface IProgramTempOwner {
  id: number;
  reason?: string | null;
  validTill?: dayjs.Dayjs | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  entityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProgramTempOwner = Omit<IProgramTempOwner, 'id'> & { id: null };
