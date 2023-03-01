import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDataValue, NewDataValue } from '../data-value.model';

export type PartialUpdateDataValue = Partial<IDataValue> & Pick<IDataValue, 'id'>;

type RestOf<T extends IDataValue | NewDataValue> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestDataValue = RestOf<IDataValue>;

export type NewRestDataValue = RestOf<NewDataValue>;

export type PartialUpdateRestDataValue = RestOf<PartialUpdateDataValue>;

export type EntityResponseType = HttpResponse<IDataValue>;
export type EntityArrayResponseType = HttpResponse<IDataValue[]>;

@Injectable({ providedIn: 'root' })
export class DataValueService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/data-values');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(dataValue: NewDataValue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataValue);
    return this.http
      .post<RestDataValue>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dataValue: IDataValue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataValue);
    return this.http
      .put<RestDataValue>(`${this.resourceUrl}/${this.getDataValueIdentifier(dataValue)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dataValue: PartialUpdateDataValue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataValue);
    return this.http
      .patch<RestDataValue>(`${this.resourceUrl}/${this.getDataValueIdentifier(dataValue)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDataValue>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDataValue[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDataValueIdentifier(dataValue: Pick<IDataValue, 'id'>): number {
    return dataValue.id;
  }

  compareDataValue(o1: Pick<IDataValue, 'id'> | null, o2: Pick<IDataValue, 'id'> | null): boolean {
    return o1 && o2 ? this.getDataValueIdentifier(o1) === this.getDataValueIdentifier(o2) : o1 === o2;
  }

  addDataValueToCollectionIfMissing<Type extends Pick<IDataValue, 'id'>>(
    dataValueCollection: Type[],
    ...dataValuesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dataValues: Type[] = dataValuesToCheck.filter(isPresent);
    if (dataValues.length > 0) {
      const dataValueCollectionIdentifiers = dataValueCollection.map(dataValueItem => this.getDataValueIdentifier(dataValueItem)!);
      const dataValuesToAdd = dataValues.filter(dataValueItem => {
        const dataValueIdentifier = this.getDataValueIdentifier(dataValueItem);
        if (dataValueCollectionIdentifiers.includes(dataValueIdentifier)) {
          return false;
        }
        dataValueCollectionIdentifiers.push(dataValueIdentifier);
        return true;
      });
      return [...dataValuesToAdd, ...dataValueCollection];
    }
    return dataValueCollection;
  }

  protected convertDateFromClient<T extends IDataValue | NewDataValue | PartialUpdateDataValue>(dataValue: T): RestOf<T> {
    return {
      ...dataValue,
      created: dataValue.created?.toJSON() ?? null,
      updated: dataValue.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDataValue: RestDataValue): IDataValue {
    return {
      ...restDataValue,
      created: restDataValue.created ? dayjs(restDataValue.created) : undefined,
      updated: restDataValue.updated ? dayjs(restDataValue.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDataValue>): HttpResponse<IDataValue> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDataValue[]>): HttpResponse<IDataValue[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
