import { IRelationshipItem, NewRelationshipItem } from './relationship-item.model';

export const sampleWithRequiredData: IRelationshipItem = {
  id: 65426,
};

export const sampleWithPartialData: IRelationshipItem = {
  id: 2521,
};

export const sampleWithFullData: IRelationshipItem = {
  id: 13418,
};

export const sampleWithNewData: NewRelationshipItem = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
