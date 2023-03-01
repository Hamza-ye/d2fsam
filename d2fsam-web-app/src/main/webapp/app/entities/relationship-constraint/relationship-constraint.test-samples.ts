import { RelationshipEntity } from 'app/entities/enumerations/relationship-entity.model';

import { IRelationshipConstraint, NewRelationshipConstraint } from './relationship-constraint.model';

export const sampleWithRequiredData: IRelationshipConstraint = {
  id: 24688,
};

export const sampleWithPartialData: IRelationshipConstraint = {
  id: 63427,
};

export const sampleWithFullData: IRelationshipConstraint = {
  id: 94261,
  relationshipEntity: RelationshipEntity['TRACKED_ENTITY_INSTANCE'],
};

export const sampleWithNewData: NewRelationshipConstraint = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
