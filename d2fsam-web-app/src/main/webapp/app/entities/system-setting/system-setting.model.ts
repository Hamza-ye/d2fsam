export interface ISystemSetting {
  id: number;
  name?: string | null;
  value?: string | null;
}

export type NewSystemSetting = Omit<ISystemSetting, 'id'> & { id: null };
