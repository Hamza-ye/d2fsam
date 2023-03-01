import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRelationshipType, NewRelationshipType } from '../relationship-type.model';

export type PartialUpdateRelationshipType = Partial<IRelationshipType> & Pick<IRelationshipType, 'id'>;

type RestOf<T extends IRelationshipType | NewRelationshipType> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestRelationshipType = RestOf<IRelationshipType>;

export type NewRestRelationshipType = RestOf<NewRelationshipType>;

export type PartialUpdateRestRelationshipType = RestOf<PartialUpdateRelationshipType>;

export type EntityResponseType = HttpResponse<IRelationshipType>;
export type EntityArrayResponseType = HttpResponse<IRelationshipType[]>;

@Injectable({ providedIn: 'root' })
export class RelationshipTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/relationship-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(relationshipType: NewRelationshipType): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(relationshipType);
    return this.http
      .post<RestRelationshipType>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(relationshipType: IRelationshipType): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(relationshipType);
    return this.http
      .put<RestRelationshipType>(`${this.resourceUrl}/${this.getRelationshipTypeIdentifier(relationshipType)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(relationshipType: PartialUpdateRelationshipType): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(relationshipType);
    return this.http
      .patch<RestRelationshipType>(`${this.resourceUrl}/${this.getRelationshipTypeIdentifier(relationshipType)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRelationshipType>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRelationshipType[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRelationshipTypeIdentifier(relationshipType: Pick<IRelationshipType, 'id'>): number {
    return relationshipType.id;
  }

  compareRelationshipType(o1: Pick<IRelationshipType, 'id'> | null, o2: Pick<IRelationshipType, 'id'> | null): boolean {
    return o1 && o2 ? this.getRelationshipTypeIdentifier(o1) === this.getRelationshipTypeIdentifier(o2) : o1 === o2;
  }

  addRelationshipTypeToCollectionIfMissing<Type extends Pick<IRelationshipType, 'id'>>(
    relationshipTypeCollection: Type[],
    ...relationshipTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const relationshipTypes: Type[] = relationshipTypesToCheck.filter(isPresent);
    if (relationshipTypes.length > 0) {
      const relationshipTypeCollectionIdentifiers = relationshipTypeCollection.map(
        relationshipTypeItem => this.getRelationshipTypeIdentifier(relationshipTypeItem)!
      );
      const relationshipTypesToAdd = relationshipTypes.filter(relationshipTypeItem => {
        const relationshipTypeIdentifier = this.getRelationshipTypeIdentifier(relationshipTypeItem);
        if (relationshipTypeCollectionIdentifiers.includes(relationshipTypeIdentifier)) {
          return false;
        }
        relationshipTypeCollectionIdentifiers.push(relationshipTypeIdentifier);
        return true;
      });
      return [...relationshipTypesToAdd, ...relationshipTypeCollection];
    }
    return relationshipTypeCollection;
  }

  protected convertDateFromClient<T extends IRelationshipType | NewRelationshipType | PartialUpdateRelationshipType>(
    relationshipType: T
  ): RestOf<T> {
    return {
      ...relationshipType,
      created: relationshipType.created?.toJSON() ?? null,
      updated: relationshipType.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRelationshipType: RestRelationshipType): IRelationshipType {
    return {
      ...restRelationshipType,
      created: restRelationshipType.created ? dayjs(restRelationshipType.created) : undefined,
      updated: restRelationshipType.updated ? dayjs(restRelationshipType.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRelationshipType>): HttpResponse<IRelationshipType> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRelationshipType[]>): HttpResponse<IRelationshipType[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
