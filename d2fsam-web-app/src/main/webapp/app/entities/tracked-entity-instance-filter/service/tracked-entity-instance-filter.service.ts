import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityInstanceFilter, NewTrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';

export type PartialUpdateTrackedEntityInstanceFilter = Partial<ITrackedEntityInstanceFilter> & Pick<ITrackedEntityInstanceFilter, 'id'>;

type RestOf<T extends ITrackedEntityInstanceFilter | NewTrackedEntityInstanceFilter> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityInstanceFilter = RestOf<ITrackedEntityInstanceFilter>;

export type NewRestTrackedEntityInstanceFilter = RestOf<NewTrackedEntityInstanceFilter>;

export type PartialUpdateRestTrackedEntityInstanceFilter = RestOf<PartialUpdateTrackedEntityInstanceFilter>;

export type EntityResponseType = HttpResponse<ITrackedEntityInstanceFilter>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityInstanceFilter[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceFilterService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-instance-filters');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityInstanceFilter: NewTrackedEntityInstanceFilter): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstanceFilter);
    return this.http
      .post<RestTrackedEntityInstanceFilter>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityInstanceFilter: ITrackedEntityInstanceFilter): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstanceFilter);
    return this.http
      .put<RestTrackedEntityInstanceFilter>(
        `${this.resourceUrl}/${this.getTrackedEntityInstanceFilterIdentifier(trackedEntityInstanceFilter)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityInstanceFilter: PartialUpdateTrackedEntityInstanceFilter): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstanceFilter);
    return this.http
      .patch<RestTrackedEntityInstanceFilter>(
        `${this.resourceUrl}/${this.getTrackedEntityInstanceFilterIdentifier(trackedEntityInstanceFilter)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityInstanceFilter>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityInstanceFilter[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityInstanceFilterIdentifier(trackedEntityInstanceFilter: Pick<ITrackedEntityInstanceFilter, 'id'>): number {
    return trackedEntityInstanceFilter.id;
  }

  compareTrackedEntityInstanceFilter(
    o1: Pick<ITrackedEntityInstanceFilter, 'id'> | null,
    o2: Pick<ITrackedEntityInstanceFilter, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTrackedEntityInstanceFilterIdentifier(o1) === this.getTrackedEntityInstanceFilterIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityInstanceFilterToCollectionIfMissing<Type extends Pick<ITrackedEntityInstanceFilter, 'id'>>(
    trackedEntityInstanceFilterCollection: Type[],
    ...trackedEntityInstanceFiltersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityInstanceFilters: Type[] = trackedEntityInstanceFiltersToCheck.filter(isPresent);
    if (trackedEntityInstanceFilters.length > 0) {
      const trackedEntityInstanceFilterCollectionIdentifiers = trackedEntityInstanceFilterCollection.map(
        trackedEntityInstanceFilterItem => this.getTrackedEntityInstanceFilterIdentifier(trackedEntityInstanceFilterItem)!
      );
      const trackedEntityInstanceFiltersToAdd = trackedEntityInstanceFilters.filter(trackedEntityInstanceFilterItem => {
        const trackedEntityInstanceFilterIdentifier = this.getTrackedEntityInstanceFilterIdentifier(trackedEntityInstanceFilterItem);
        if (trackedEntityInstanceFilterCollectionIdentifiers.includes(trackedEntityInstanceFilterIdentifier)) {
          return false;
        }
        trackedEntityInstanceFilterCollectionIdentifiers.push(trackedEntityInstanceFilterIdentifier);
        return true;
      });
      return [...trackedEntityInstanceFiltersToAdd, ...trackedEntityInstanceFilterCollection];
    }
    return trackedEntityInstanceFilterCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityInstanceFilter | NewTrackedEntityInstanceFilter | PartialUpdateTrackedEntityInstanceFilter
  >(trackedEntityInstanceFilter: T): RestOf<T> {
    return {
      ...trackedEntityInstanceFilter,
      created: trackedEntityInstanceFilter.created?.toJSON() ?? null,
      updated: trackedEntityInstanceFilter.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityInstanceFilter: RestTrackedEntityInstanceFilter): ITrackedEntityInstanceFilter {
    return {
      ...restTrackedEntityInstanceFilter,
      created: restTrackedEntityInstanceFilter.created ? dayjs(restTrackedEntityInstanceFilter.created) : undefined,
      updated: restTrackedEntityInstanceFilter.updated ? dayjs(restTrackedEntityInstanceFilter.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityInstanceFilter>): HttpResponse<ITrackedEntityInstanceFilter> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestTrackedEntityInstanceFilter[]>
  ): HttpResponse<ITrackedEntityInstanceFilter[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
