import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { IProgram } from 'app/entities/program/program.model';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { RelationshipEntity } from 'app/entities/enumerations/relationship-entity.model';

export interface IRelationshipConstraint {
  id: number;
  relationshipEntity?: RelationshipEntity | null;
  trackedEntityType?: Pick<ITrackedEntityType, 'id' | 'name'> | null;
  program?: Pick<IProgram, 'id' | 'name'> | null;
  programStage?: Pick<IProgramStage, 'id' | 'name'> | null;
}

export type NewRelationshipConstraint = Omit<IRelationshipConstraint, 'id'> & { id: null };
