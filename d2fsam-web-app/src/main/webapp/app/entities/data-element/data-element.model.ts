import dayjs from 'dayjs/esm';
import { IOptionSet } from 'app/entities/option-set/option-set.model';
import { IUser } from 'app/entities/user/user.model';
import { ValueType } from 'app/entities/enumerations/value-type.model';
import { AggregationType } from 'app/entities/enumerations/aggregation-type.model';

export interface IDataElement {
  id: number;
  uid?: string | null;
  code?: string | null;
  created?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  name?: string | null;
  description?: string | null;
  valueType?: ValueType | null;
  aggregationType?: AggregationType | null;
  displayInListNoProgram?: boolean | null;
  zeroIsSignificant?: boolean | null;
  mandatory?: boolean | null;
  uniqueAttribute?: boolean | null;
  fieldMask?: string | null;
  orgunitScope?: boolean | null;
  skipSynchronization?: boolean | null;
  confidential?: boolean | null;
  optionSet?: Pick<IOptionSet, 'id' | 'name'> | null;
  createdBy?: Pick<IUser, 'id' | 'login'> | null;
  updatedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDataElement = Omit<IDataElement, 'id'> & { id: null };
