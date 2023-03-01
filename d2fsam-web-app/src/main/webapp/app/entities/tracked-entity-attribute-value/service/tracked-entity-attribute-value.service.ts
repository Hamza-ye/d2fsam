import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityAttributeValue, NewTrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';

export type PartialUpdateTrackedEntityAttributeValue = Partial<ITrackedEntityAttributeValue> & Pick<ITrackedEntityAttributeValue, 'id'>;

type RestOf<T extends ITrackedEntityAttributeValue | NewTrackedEntityAttributeValue> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityAttributeValue = RestOf<ITrackedEntityAttributeValue>;

export type NewRestTrackedEntityAttributeValue = RestOf<NewTrackedEntityAttributeValue>;

export type PartialUpdateRestTrackedEntityAttributeValue = RestOf<PartialUpdateTrackedEntityAttributeValue>;

export type EntityResponseType = HttpResponse<ITrackedEntityAttributeValue>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityAttributeValue[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityAttributeValueService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-attribute-values');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityAttributeValue: NewTrackedEntityAttributeValue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityAttributeValue);
    return this.http
      .post<RestTrackedEntityAttributeValue>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityAttributeValue: ITrackedEntityAttributeValue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityAttributeValue);
    return this.http
      .put<RestTrackedEntityAttributeValue>(
        `${this.resourceUrl}/${this.getTrackedEntityAttributeValueIdentifier(trackedEntityAttributeValue)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityAttributeValue: PartialUpdateTrackedEntityAttributeValue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityAttributeValue);
    return this.http
      .patch<RestTrackedEntityAttributeValue>(
        `${this.resourceUrl}/${this.getTrackedEntityAttributeValueIdentifier(trackedEntityAttributeValue)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityAttributeValue>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityAttributeValue[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityAttributeValueIdentifier(trackedEntityAttributeValue: Pick<ITrackedEntityAttributeValue, 'id'>): number {
    return trackedEntityAttributeValue.id;
  }

  compareTrackedEntityAttributeValue(
    o1: Pick<ITrackedEntityAttributeValue, 'id'> | null,
    o2: Pick<ITrackedEntityAttributeValue, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTrackedEntityAttributeValueIdentifier(o1) === this.getTrackedEntityAttributeValueIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityAttributeValueToCollectionIfMissing<Type extends Pick<ITrackedEntityAttributeValue, 'id'>>(
    trackedEntityAttributeValueCollection: Type[],
    ...trackedEntityAttributeValuesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityAttributeValues: Type[] = trackedEntityAttributeValuesToCheck.filter(isPresent);
    if (trackedEntityAttributeValues.length > 0) {
      const trackedEntityAttributeValueCollectionIdentifiers = trackedEntityAttributeValueCollection.map(
        trackedEntityAttributeValueItem => this.getTrackedEntityAttributeValueIdentifier(trackedEntityAttributeValueItem)!
      );
      const trackedEntityAttributeValuesToAdd = trackedEntityAttributeValues.filter(trackedEntityAttributeValueItem => {
        const trackedEntityAttributeValueIdentifier = this.getTrackedEntityAttributeValueIdentifier(trackedEntityAttributeValueItem);
        if (trackedEntityAttributeValueCollectionIdentifiers.includes(trackedEntityAttributeValueIdentifier)) {
          return false;
        }
        trackedEntityAttributeValueCollectionIdentifiers.push(trackedEntityAttributeValueIdentifier);
        return true;
      });
      return [...trackedEntityAttributeValuesToAdd, ...trackedEntityAttributeValueCollection];
    }
    return trackedEntityAttributeValueCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityAttributeValue | NewTrackedEntityAttributeValue | PartialUpdateTrackedEntityAttributeValue
  >(trackedEntityAttributeValue: T): RestOf<T> {
    return {
      ...trackedEntityAttributeValue,
      created: trackedEntityAttributeValue.created?.toJSON() ?? null,
      updated: trackedEntityAttributeValue.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityAttributeValue: RestTrackedEntityAttributeValue): ITrackedEntityAttributeValue {
    return {
      ...restTrackedEntityAttributeValue,
      created: restTrackedEntityAttributeValue.created ? dayjs(restTrackedEntityAttributeValue.created) : undefined,
      updated: restTrackedEntityAttributeValue.updated ? dayjs(restTrackedEntityAttributeValue.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityAttributeValue>): HttpResponse<ITrackedEntityAttributeValue> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestTrackedEntityAttributeValue[]>
  ): HttpResponse<ITrackedEntityAttributeValue[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
