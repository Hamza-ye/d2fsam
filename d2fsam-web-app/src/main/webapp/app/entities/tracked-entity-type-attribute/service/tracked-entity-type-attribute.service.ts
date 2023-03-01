import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityTypeAttribute, NewTrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';

export type PartialUpdateTrackedEntityTypeAttribute = Partial<ITrackedEntityTypeAttribute> & Pick<ITrackedEntityTypeAttribute, 'id'>;

type RestOf<T extends ITrackedEntityTypeAttribute | NewTrackedEntityTypeAttribute> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityTypeAttribute = RestOf<ITrackedEntityTypeAttribute>;

export type NewRestTrackedEntityTypeAttribute = RestOf<NewTrackedEntityTypeAttribute>;

export type PartialUpdateRestTrackedEntityTypeAttribute = RestOf<PartialUpdateTrackedEntityTypeAttribute>;

export type EntityResponseType = HttpResponse<ITrackedEntityTypeAttribute>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityTypeAttribute[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityTypeAttributeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-type-attributes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityTypeAttribute: NewTrackedEntityTypeAttribute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityTypeAttribute);
    return this.http
      .post<RestTrackedEntityTypeAttribute>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityTypeAttribute: ITrackedEntityTypeAttribute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityTypeAttribute);
    return this.http
      .put<RestTrackedEntityTypeAttribute>(
        `${this.resourceUrl}/${this.getTrackedEntityTypeAttributeIdentifier(trackedEntityTypeAttribute)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityTypeAttribute: PartialUpdateTrackedEntityTypeAttribute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityTypeAttribute);
    return this.http
      .patch<RestTrackedEntityTypeAttribute>(
        `${this.resourceUrl}/${this.getTrackedEntityTypeAttributeIdentifier(trackedEntityTypeAttribute)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityTypeAttribute>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityTypeAttribute[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityTypeAttributeIdentifier(trackedEntityTypeAttribute: Pick<ITrackedEntityTypeAttribute, 'id'>): number {
    return trackedEntityTypeAttribute.id;
  }

  compareTrackedEntityTypeAttribute(
    o1: Pick<ITrackedEntityTypeAttribute, 'id'> | null,
    o2: Pick<ITrackedEntityTypeAttribute, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTrackedEntityTypeAttributeIdentifier(o1) === this.getTrackedEntityTypeAttributeIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityTypeAttributeToCollectionIfMissing<Type extends Pick<ITrackedEntityTypeAttribute, 'id'>>(
    trackedEntityTypeAttributeCollection: Type[],
    ...trackedEntityTypeAttributesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityTypeAttributes: Type[] = trackedEntityTypeAttributesToCheck.filter(isPresent);
    if (trackedEntityTypeAttributes.length > 0) {
      const trackedEntityTypeAttributeCollectionIdentifiers = trackedEntityTypeAttributeCollection.map(
        trackedEntityTypeAttributeItem => this.getTrackedEntityTypeAttributeIdentifier(trackedEntityTypeAttributeItem)!
      );
      const trackedEntityTypeAttributesToAdd = trackedEntityTypeAttributes.filter(trackedEntityTypeAttributeItem => {
        const trackedEntityTypeAttributeIdentifier = this.getTrackedEntityTypeAttributeIdentifier(trackedEntityTypeAttributeItem);
        if (trackedEntityTypeAttributeCollectionIdentifiers.includes(trackedEntityTypeAttributeIdentifier)) {
          return false;
        }
        trackedEntityTypeAttributeCollectionIdentifiers.push(trackedEntityTypeAttributeIdentifier);
        return true;
      });
      return [...trackedEntityTypeAttributesToAdd, ...trackedEntityTypeAttributeCollection];
    }
    return trackedEntityTypeAttributeCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityTypeAttribute | NewTrackedEntityTypeAttribute | PartialUpdateTrackedEntityTypeAttribute
  >(trackedEntityTypeAttribute: T): RestOf<T> {
    return {
      ...trackedEntityTypeAttribute,
      created: trackedEntityTypeAttribute.created?.toJSON() ?? null,
      updated: trackedEntityTypeAttribute.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityTypeAttribute: RestTrackedEntityTypeAttribute): ITrackedEntityTypeAttribute {
    return {
      ...restTrackedEntityTypeAttribute,
      created: restTrackedEntityTypeAttribute.created ? dayjs(restTrackedEntityTypeAttribute.created) : undefined,
      updated: restTrackedEntityTypeAttribute.updated ? dayjs(restTrackedEntityTypeAttribute.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityTypeAttribute>): HttpResponse<ITrackedEntityTypeAttribute> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestTrackedEntityTypeAttribute[]>
  ): HttpResponse<ITrackedEntityTypeAttribute[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
