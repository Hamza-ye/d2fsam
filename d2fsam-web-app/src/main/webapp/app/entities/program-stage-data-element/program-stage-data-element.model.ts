import dayjs from 'dayjs/esm';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { IUser } from 'app/entities/user/user.model';

export interface IProgramStageDataElement {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  compulsory?: boolean | null;
  allowProvidedElsewhere?: boolean | null;
  sortOrder?: number | null;
  displayInReports?: boolean | null;
  allowFutureDate?: boolean | null;
  skipSynchronization?: boolean | null;
  defaultValue?: string | null;
  programStage?: Pick<IProgramStage, 'id' | 'name'> | null;
  dataElement?: Pick<IDataElement, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProgramStageDataElement = Omit<IProgramStageDataElement, 'id'> & { id: null };
