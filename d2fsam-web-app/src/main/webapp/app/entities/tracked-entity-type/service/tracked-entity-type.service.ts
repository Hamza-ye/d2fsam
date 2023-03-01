import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityType, NewTrackedEntityType } from '../tracked-entity-type.model';

export type PartialUpdateTrackedEntityType = Partial<ITrackedEntityType> & Pick<ITrackedEntityType, 'id'>;

type RestOf<T extends ITrackedEntityType | NewTrackedEntityType> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityType = RestOf<ITrackedEntityType>;

export type NewRestTrackedEntityType = RestOf<NewTrackedEntityType>;

export type PartialUpdateRestTrackedEntityType = RestOf<PartialUpdateTrackedEntityType>;

export type EntityResponseType = HttpResponse<ITrackedEntityType>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityType[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityType: NewTrackedEntityType): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityType);
    return this.http
      .post<RestTrackedEntityType>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityType: ITrackedEntityType): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityType);
    return this.http
      .put<RestTrackedEntityType>(`${this.resourceUrl}/${this.getTrackedEntityTypeIdentifier(trackedEntityType)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityType: PartialUpdateTrackedEntityType): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityType);
    return this.http
      .patch<RestTrackedEntityType>(`${this.resourceUrl}/${this.getTrackedEntityTypeIdentifier(trackedEntityType)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityType>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityType[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityTypeIdentifier(trackedEntityType: Pick<ITrackedEntityType, 'id'>): number {
    return trackedEntityType.id;
  }

  compareTrackedEntityType(o1: Pick<ITrackedEntityType, 'id'> | null, o2: Pick<ITrackedEntityType, 'id'> | null): boolean {
    return o1 && o2 ? this.getTrackedEntityTypeIdentifier(o1) === this.getTrackedEntityTypeIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityTypeToCollectionIfMissing<Type extends Pick<ITrackedEntityType, 'id'>>(
    trackedEntityTypeCollection: Type[],
    ...trackedEntityTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityTypes: Type[] = trackedEntityTypesToCheck.filter(isPresent);
    if (trackedEntityTypes.length > 0) {
      const trackedEntityTypeCollectionIdentifiers = trackedEntityTypeCollection.map(
        trackedEntityTypeItem => this.getTrackedEntityTypeIdentifier(trackedEntityTypeItem)!
      );
      const trackedEntityTypesToAdd = trackedEntityTypes.filter(trackedEntityTypeItem => {
        const trackedEntityTypeIdentifier = this.getTrackedEntityTypeIdentifier(trackedEntityTypeItem);
        if (trackedEntityTypeCollectionIdentifiers.includes(trackedEntityTypeIdentifier)) {
          return false;
        }
        trackedEntityTypeCollectionIdentifiers.push(trackedEntityTypeIdentifier);
        return true;
      });
      return [...trackedEntityTypesToAdd, ...trackedEntityTypeCollection];
    }
    return trackedEntityTypeCollection;
  }

  protected convertDateFromClient<T extends ITrackedEntityType | NewTrackedEntityType | PartialUpdateTrackedEntityType>(
    trackedEntityType: T
  ): RestOf<T> {
    return {
      ...trackedEntityType,
      created: trackedEntityType.created?.toJSON() ?? null,
      updated: trackedEntityType.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityType: RestTrackedEntityType): ITrackedEntityType {
    return {
      ...restTrackedEntityType,
      created: restTrackedEntityType.created ? dayjs(restTrackedEntityType.created) : undefined,
      updated: restTrackedEntityType.updated ? dayjs(restTrackedEntityType.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityType>): HttpResponse<ITrackedEntityType> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTrackedEntityType[]>): HttpResponse<ITrackedEntityType[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
