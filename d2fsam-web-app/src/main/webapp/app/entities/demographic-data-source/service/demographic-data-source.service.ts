import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDemographicDataSource, NewDemographicDataSource } from '../demographic-data-source.model';

export type PartialUpdateDemographicDataSource = Partial<IDemographicDataSource> & Pick<IDemographicDataSource, 'id'>;

type RestOf<T extends IDemographicDataSource | NewDemographicDataSource> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestDemographicDataSource = RestOf<IDemographicDataSource>;

export type NewRestDemographicDataSource = RestOf<NewDemographicDataSource>;

export type PartialUpdateRestDemographicDataSource = RestOf<PartialUpdateDemographicDataSource>;

export type EntityResponseType = HttpResponse<IDemographicDataSource>;
export type EntityArrayResponseType = HttpResponse<IDemographicDataSource[]>;

@Injectable({ providedIn: 'root' })
export class DemographicDataSourceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/demographic-data-sources');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(demographicDataSource: NewDemographicDataSource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demographicDataSource);
    return this.http
      .post<RestDemographicDataSource>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(demographicDataSource: IDemographicDataSource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demographicDataSource);
    return this.http
      .put<RestDemographicDataSource>(`${this.resourceUrl}/${this.getDemographicDataSourceIdentifier(demographicDataSource)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(demographicDataSource: PartialUpdateDemographicDataSource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demographicDataSource);
    return this.http
      .patch<RestDemographicDataSource>(`${this.resourceUrl}/${this.getDemographicDataSourceIdentifier(demographicDataSource)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDemographicDataSource>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDemographicDataSource[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDemographicDataSourceIdentifier(demographicDataSource: Pick<IDemographicDataSource, 'id'>): number {
    return demographicDataSource.id;
  }

  compareDemographicDataSource(o1: Pick<IDemographicDataSource, 'id'> | null, o2: Pick<IDemographicDataSource, 'id'> | null): boolean {
    return o1 && o2 ? this.getDemographicDataSourceIdentifier(o1) === this.getDemographicDataSourceIdentifier(o2) : o1 === o2;
  }

  addDemographicDataSourceToCollectionIfMissing<Type extends Pick<IDemographicDataSource, 'id'>>(
    demographicDataSourceCollection: Type[],
    ...demographicDataSourcesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const demographicDataSources: Type[] = demographicDataSourcesToCheck.filter(isPresent);
    if (demographicDataSources.length > 0) {
      const demographicDataSourceCollectionIdentifiers = demographicDataSourceCollection.map(
        demographicDataSourceItem => this.getDemographicDataSourceIdentifier(demographicDataSourceItem)!
      );
      const demographicDataSourcesToAdd = demographicDataSources.filter(demographicDataSourceItem => {
        const demographicDataSourceIdentifier = this.getDemographicDataSourceIdentifier(demographicDataSourceItem);
        if (demographicDataSourceCollectionIdentifiers.includes(demographicDataSourceIdentifier)) {
          return false;
        }
        demographicDataSourceCollectionIdentifiers.push(demographicDataSourceIdentifier);
        return true;
      });
      return [...demographicDataSourcesToAdd, ...demographicDataSourceCollection];
    }
    return demographicDataSourceCollection;
  }

  protected convertDateFromClient<T extends IDemographicDataSource | NewDemographicDataSource | PartialUpdateDemographicDataSource>(
    demographicDataSource: T
  ): RestOf<T> {
    return {
      ...demographicDataSource,
      created: demographicDataSource.created?.toJSON() ?? null,
      updated: demographicDataSource.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDemographicDataSource: RestDemographicDataSource): IDemographicDataSource {
    return {
      ...restDemographicDataSource,
      created: restDemographicDataSource.created ? dayjs(restDemographicDataSource.created) : undefined,
      updated: restDemographicDataSource.updated ? dayjs(restDemographicDataSource.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDemographicDataSource>): HttpResponse<IDemographicDataSource> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDemographicDataSource[]>): HttpResponse<IDemographicDataSource[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
