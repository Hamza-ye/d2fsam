import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDemographicData, NewDemographicData } from '../demographic-data.model';

export type PartialUpdateDemographicData = Partial<IDemographicData> & Pick<IDemographicData, 'id'>;

type RestOf<T extends IDemographicData | NewDemographicData> = Omit<T, 'created' | 'updated' | 'date'> & {
  created?: string | null;
  updated?: string | null;
  date?: string | null;
};

export type RestDemographicData = RestOf<IDemographicData>;

export type NewRestDemographicData = RestOf<NewDemographicData>;

export type PartialUpdateRestDemographicData = RestOf<PartialUpdateDemographicData>;

export type EntityResponseType = HttpResponse<IDemographicData>;
export type EntityArrayResponseType = HttpResponse<IDemographicData[]>;

@Injectable({ providedIn: 'root' })
export class DemographicDataService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/demographic-data');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(demographicData: NewDemographicData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demographicData);
    return this.http
      .post<RestDemographicData>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(demographicData: IDemographicData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demographicData);
    return this.http
      .put<RestDemographicData>(`${this.resourceUrl}/${this.getDemographicDataIdentifier(demographicData)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(demographicData: PartialUpdateDemographicData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demographicData);
    return this.http
      .patch<RestDemographicData>(`${this.resourceUrl}/${this.getDemographicDataIdentifier(demographicData)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDemographicData>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDemographicData[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDemographicDataIdentifier(demographicData: Pick<IDemographicData, 'id'>): number {
    return demographicData.id;
  }

  compareDemographicData(o1: Pick<IDemographicData, 'id'> | null, o2: Pick<IDemographicData, 'id'> | null): boolean {
    return o1 && o2 ? this.getDemographicDataIdentifier(o1) === this.getDemographicDataIdentifier(o2) : o1 === o2;
  }

  addDemographicDataToCollectionIfMissing<Type extends Pick<IDemographicData, 'id'>>(
    demographicDataCollection: Type[],
    ...demographicDataToCheck: (Type | null | undefined)[]
  ): Type[] {
    const demographicData: Type[] = demographicDataToCheck.filter(isPresent);
    if (demographicData.length > 0) {
      const demographicDataCollectionIdentifiers = demographicDataCollection.map(
        demographicDataItem => this.getDemographicDataIdentifier(demographicDataItem)!
      );
      const demographicDataToAdd = demographicData.filter(demographicDataItem => {
        const demographicDataIdentifier = this.getDemographicDataIdentifier(demographicDataItem);
        if (demographicDataCollectionIdentifiers.includes(demographicDataIdentifier)) {
          return false;
        }
        demographicDataCollectionIdentifiers.push(demographicDataIdentifier);
        return true;
      });
      return [...demographicDataToAdd, ...demographicDataCollection];
    }
    return demographicDataCollection;
  }

  protected convertDateFromClient<T extends IDemographicData | NewDemographicData | PartialUpdateDemographicData>(
    demographicData: T
  ): RestOf<T> {
    return {
      ...demographicData,
      created: demographicData.created?.toJSON() ?? null,
      updated: demographicData.updated?.toJSON() ?? null,
      date: demographicData.date?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDemographicData: RestDemographicData): IDemographicData {
    return {
      ...restDemographicData,
      created: restDemographicData.created ? dayjs(restDemographicData.created) : undefined,
      updated: restDemographicData.updated ? dayjs(restDemographicData.updated) : undefined,
      date: restDemographicData.date ? dayjs(restDemographicData.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDemographicData>): HttpResponse<IDemographicData> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDemographicData[]>): HttpResponse<IDemographicData[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
