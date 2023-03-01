import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRelationshipItem, NewRelationshipItem } from '../relationship-item.model';

export type PartialUpdateRelationshipItem = Partial<IRelationshipItem> & Pick<IRelationshipItem, 'id'>;

export type EntityResponseType = HttpResponse<IRelationshipItem>;
export type EntityArrayResponseType = HttpResponse<IRelationshipItem[]>;

@Injectable({ providedIn: 'root' })
export class RelationshipItemService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/relationship-items');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(relationshipItem: NewRelationshipItem): Observable<EntityResponseType> {
    return this.http.post<IRelationshipItem>(this.resourceUrl, relationshipItem, { observe: 'response' });
  }

  update(relationshipItem: IRelationshipItem): Observable<EntityResponseType> {
    return this.http.put<IRelationshipItem>(
      `${this.resourceUrl}/${this.getRelationshipItemIdentifier(relationshipItem)}`,
      relationshipItem,
      { observe: 'response' }
    );
  }

  partialUpdate(relationshipItem: PartialUpdateRelationshipItem): Observable<EntityResponseType> {
    return this.http.patch<IRelationshipItem>(
      `${this.resourceUrl}/${this.getRelationshipItemIdentifier(relationshipItem)}`,
      relationshipItem,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRelationshipItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelationshipItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRelationshipItemIdentifier(relationshipItem: Pick<IRelationshipItem, 'id'>): number {
    return relationshipItem.id;
  }

  compareRelationshipItem(o1: Pick<IRelationshipItem, 'id'> | null, o2: Pick<IRelationshipItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getRelationshipItemIdentifier(o1) === this.getRelationshipItemIdentifier(o2) : o1 === o2;
  }

  addRelationshipItemToCollectionIfMissing<Type extends Pick<IRelationshipItem, 'id'>>(
    relationshipItemCollection: Type[],
    ...relationshipItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const relationshipItems: Type[] = relationshipItemsToCheck.filter(isPresent);
    if (relationshipItems.length > 0) {
      const relationshipItemCollectionIdentifiers = relationshipItemCollection.map(
        relationshipItemItem => this.getRelationshipItemIdentifier(relationshipItemItem)!
      );
      const relationshipItemsToAdd = relationshipItems.filter(relationshipItemItem => {
        const relationshipItemIdentifier = this.getRelationshipItemIdentifier(relationshipItemItem);
        if (relationshipItemCollectionIdentifiers.includes(relationshipItemIdentifier)) {
          return false;
        }
        relationshipItemCollectionIdentifiers.push(relationshipItemIdentifier);
        return true;
      });
      return [...relationshipItemsToAdd, ...relationshipItemCollection];
    }
    return relationshipItemCollection;
  }
}
