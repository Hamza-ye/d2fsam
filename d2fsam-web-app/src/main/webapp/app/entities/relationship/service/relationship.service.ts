import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRelationship, NewRelationship } from '../relationship.model';

export type PartialUpdateRelationship = Partial<IRelationship> & Pick<IRelationship, 'id'>;

type RestOf<T extends IRelationship | NewRelationship> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestRelationship = RestOf<IRelationship>;

export type NewRestRelationship = RestOf<NewRelationship>;

export type PartialUpdateRestRelationship = RestOf<PartialUpdateRelationship>;

export type EntityResponseType = HttpResponse<IRelationship>;
export type EntityArrayResponseType = HttpResponse<IRelationship[]>;

@Injectable({ providedIn: 'root' })
export class RelationshipService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/relationships');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(relationship: NewRelationship): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(relationship);
    return this.http
      .post<RestRelationship>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(relationship: IRelationship): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(relationship);
    return this.http
      .put<RestRelationship>(`${this.resourceUrl}/${this.getRelationshipIdentifier(relationship)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(relationship: PartialUpdateRelationship): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(relationship);
    return this.http
      .patch<RestRelationship>(`${this.resourceUrl}/${this.getRelationshipIdentifier(relationship)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRelationship>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRelationship[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRelationshipIdentifier(relationship: Pick<IRelationship, 'id'>): number {
    return relationship.id;
  }

  compareRelationship(o1: Pick<IRelationship, 'id'> | null, o2: Pick<IRelationship, 'id'> | null): boolean {
    return o1 && o2 ? this.getRelationshipIdentifier(o1) === this.getRelationshipIdentifier(o2) : o1 === o2;
  }

  addRelationshipToCollectionIfMissing<Type extends Pick<IRelationship, 'id'>>(
    relationshipCollection: Type[],
    ...relationshipsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const relationships: Type[] = relationshipsToCheck.filter(isPresent);
    if (relationships.length > 0) {
      const relationshipCollectionIdentifiers = relationshipCollection.map(
        relationshipItem => this.getRelationshipIdentifier(relationshipItem)!
      );
      const relationshipsToAdd = relationships.filter(relationshipItem => {
        const relationshipIdentifier = this.getRelationshipIdentifier(relationshipItem);
        if (relationshipCollectionIdentifiers.includes(relationshipIdentifier)) {
          return false;
        }
        relationshipCollectionIdentifiers.push(relationshipIdentifier);
        return true;
      });
      return [...relationshipsToAdd, ...relationshipCollection];
    }
    return relationshipCollection;
  }

  protected convertDateFromClient<T extends IRelationship | NewRelationship | PartialUpdateRelationship>(relationship: T): RestOf<T> {
    return {
      ...relationship,
      created: relationship.created?.toJSON() ?? null,
      updated: relationship.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRelationship: RestRelationship): IRelationship {
    return {
      ...restRelationship,
      created: restRelationship.created ? dayjs(restRelationship.created) : undefined,
      updated: restRelationship.updated ? dayjs(restRelationship.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRelationship>): HttpResponse<IRelationship> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRelationship[]>): HttpResponse<IRelationship[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
