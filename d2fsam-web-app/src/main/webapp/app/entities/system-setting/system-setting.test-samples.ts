import { ISystemSetting, NewSystemSetting } from './system-setting.model';

export const sampleWithRequiredData: ISystemSetting = {
  id: 88075,
};

export const sampleWithPartialData: ISystemSetting = {
  id: 77226,
  name: 'navigate',
  value: 'Functionality',
};

export const sampleWithFullData: ISystemSetting = {
  id: 10842,
  name: 'Awesome',
  value: 'Operations',
};

export const sampleWithNewData: NewSystemSetting = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
