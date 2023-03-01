import dayjs from 'dayjs/esm';

import { IComment, NewComment } from './comment.model';

export const sampleWithRequiredData: IComment = {
  id: 78899,
};

export const sampleWithPartialData: IComment = {
  id: 56305,
  code: 'invoice',
  commentText: 'web-readiness repurpose',
};

export const sampleWithFullData: IComment = {
  id: 63619,
  uid: 'Estate',
  code: 'Investment Macedonia Creative',
  created: dayjs('2022-11-19T23:42'),
  updated: dayjs('2022-11-20T09:49'),
  commentText: 'Soft tertiary withdrawal',
  creator: 'Sports deposit',
};

export const sampleWithNewData: NewComment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
