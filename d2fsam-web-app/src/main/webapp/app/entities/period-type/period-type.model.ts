export interface IPeriodType {
  id: number;
  name?: string | null;
}

export type NewPeriodType = Omit<IPeriodType, 'id'> & { id: null };
