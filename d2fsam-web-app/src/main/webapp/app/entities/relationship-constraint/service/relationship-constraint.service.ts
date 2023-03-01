import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRelationshipConstraint, NewRelationshipConstraint } from '../relationship-constraint.model';

export type PartialUpdateRelationshipConstraint = Partial<IRelationshipConstraint> & Pick<IRelationshipConstraint, 'id'>;

export type EntityResponseType = HttpResponse<IRelationshipConstraint>;
export type EntityArrayResponseType = HttpResponse<IRelationshipConstraint[]>;

@Injectable({ providedIn: 'root' })
export class RelationshipConstraintService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/relationship-constraints');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(relationshipConstraint: NewRelationshipConstraint): Observable<EntityResponseType> {
    return this.http.post<IRelationshipConstraint>(this.resourceUrl, relationshipConstraint, { observe: 'response' });
  }

  update(relationshipConstraint: IRelationshipConstraint): Observable<EntityResponseType> {
    return this.http.put<IRelationshipConstraint>(
      `${this.resourceUrl}/${this.getRelationshipConstraintIdentifier(relationshipConstraint)}`,
      relationshipConstraint,
      { observe: 'response' }
    );
  }

  partialUpdate(relationshipConstraint: PartialUpdateRelationshipConstraint): Observable<EntityResponseType> {
    return this.http.patch<IRelationshipConstraint>(
      `${this.resourceUrl}/${this.getRelationshipConstraintIdentifier(relationshipConstraint)}`,
      relationshipConstraint,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRelationshipConstraint>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelationshipConstraint[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRelationshipConstraintIdentifier(relationshipConstraint: Pick<IRelationshipConstraint, 'id'>): number {
    return relationshipConstraint.id;
  }

  compareRelationshipConstraint(o1: Pick<IRelationshipConstraint, 'id'> | null, o2: Pick<IRelationshipConstraint, 'id'> | null): boolean {
    return o1 && o2 ? this.getRelationshipConstraintIdentifier(o1) === this.getRelationshipConstraintIdentifier(o2) : o1 === o2;
  }

  addRelationshipConstraintToCollectionIfMissing<Type extends Pick<IRelationshipConstraint, 'id'>>(
    relationshipConstraintCollection: Type[],
    ...relationshipConstraintsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const relationshipConstraints: Type[] = relationshipConstraintsToCheck.filter(isPresent);
    if (relationshipConstraints.length > 0) {
      const relationshipConstraintCollectionIdentifiers = relationshipConstraintCollection.map(
        relationshipConstraintItem => this.getRelationshipConstraintIdentifier(relationshipConstraintItem)!
      );
      const relationshipConstraintsToAdd = relationshipConstraints.filter(relationshipConstraintItem => {
        const relationshipConstraintIdentifier = this.getRelationshipConstraintIdentifier(relationshipConstraintItem);
        if (relationshipConstraintCollectionIdentifiers.includes(relationshipConstraintIdentifier)) {
          return false;
        }
        relationshipConstraintCollectionIdentifiers.push(relationshipConstraintIdentifier);
        return true;
      });
      return [...relationshipConstraintsToAdd, ...relationshipConstraintCollection];
    }
    return relationshipConstraintCollection;
  }
}
