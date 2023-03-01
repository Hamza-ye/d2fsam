import dayjs from 'dayjs/esm';

import { IDocument, NewDocument } from './document.model';

export const sampleWithRequiredData: IDocument = {
  id: 70614,
  uid: 'Planner Bor',
};

export const sampleWithPartialData: IDocument = {
  id: 37803,
  uid: 'Future',
  code: 'connect',
  created: dayjs('2022-11-20T03:35'),
  updated: dayjs('2022-11-20T16:23'),
  contentType: 'Handcrafted Object-based',
  attachment: false,
};

export const sampleWithFullData: IDocument = {
  id: 97944,
  uid: 'payment Hai',
  code: 'back revolutionary Applications',
  name: 'neural benchmark pixel',
  created: dayjs('2022-11-20T01:40'),
  updated: dayjs('2022-11-20T16:37'),
  url: 'http://juvenal.com',
  external: true,
  contentType: 'Dollar Indian syndicate',
  attachment: true,
};

export const sampleWithNewData: NewDocument = {
  uid: 'payment',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
