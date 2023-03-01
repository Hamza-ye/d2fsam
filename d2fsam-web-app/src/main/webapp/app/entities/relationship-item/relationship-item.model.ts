import { IRelationship } from 'app/entities/relationship/relationship.model';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';

export interface IRelationshipItem {
  id: number;
  relationship?: Pick<IRelationship, 'id' | 'uid'> | null;
  trackedEntityInstance?: Pick<ITrackedEntityInstance, 'id' | 'uid'> | null;
  programInstance?: Pick<IProgramInstance, 'id' | 'uid'> | null;
  programStageInstance?: Pick<IProgramStageInstance, 'id' | 'uid'> | null;
}

export type NewRelationshipItem = Omit<IRelationshipItem, 'id'> & { id: null };
