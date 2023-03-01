import dayjs from 'dayjs/esm';

import { FileResourceDomain } from 'app/entities/enumerations/file-resource-domain.model';

import { IFileResource, NewFileResource } from './file-resource.model';

export const sampleWithRequiredData: IFileResource = {
  id: 79712,
  uid: 'Savings',
};

export const sampleWithPartialData: IFileResource = {
  id: 83503,
  uid: 'concept Ste',
  name: 'Idaho applications Chips',
  created: dayjs('2022-11-20T18:49'),
  contentLength: 42289,
  contentMd5: 'microchip',
  storageKey: 'Assistant',
  domain: FileResourceDomain['USER_AVATAR'],
  hasMultipleStorageFiles: true,
};

export const sampleWithFullData: IFileResource = {
  id: 91797,
  uid: 'systemic re',
  code: 'California quantifying',
  name: 'Implementation quantifying SMS',
  created: dayjs('2022-11-20T00:33'),
  updated: dayjs('2022-11-20T19:58'),
  contentType: 'orchid primary',
  contentLength: 70595,
  contentMd5: 'Frozen',
  storageKey: 'definition',
  assigned: true,
  domain: FileResourceDomain['DOCUMENT'],
  hasMultipleStorageFiles: false,
  fileResourceOwner: 'Rubber bandwidth Iraqi',
};

export const sampleWithNewData: NewFileResource = {
  uid: 'Sleek olive',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
