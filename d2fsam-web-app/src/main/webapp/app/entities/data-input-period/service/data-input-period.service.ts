import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDataInputPeriod, NewDataInputPeriod } from '../data-input-period.model';

export type PartialUpdateDataInputPeriod = Partial<IDataInputPeriod> & Pick<IDataInputPeriod, 'id'>;

type RestOf<T extends IDataInputPeriod | NewDataInputPeriod> = Omit<T, 'openingDate' | 'closingDate'> & {
  openingDate?: string | null;
  closingDate?: string | null;
};

export type RestDataInputPeriod = RestOf<IDataInputPeriod>;

export type NewRestDataInputPeriod = RestOf<NewDataInputPeriod>;

export type PartialUpdateRestDataInputPeriod = RestOf<PartialUpdateDataInputPeriod>;

export type EntityResponseType = HttpResponse<IDataInputPeriod>;
export type EntityArrayResponseType = HttpResponse<IDataInputPeriod[]>;

@Injectable({ providedIn: 'root' })
export class DataInputPeriodService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/data-input-periods');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(dataInputPeriod: NewDataInputPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataInputPeriod);
    return this.http
      .post<RestDataInputPeriod>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dataInputPeriod: IDataInputPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataInputPeriod);
    return this.http
      .put<RestDataInputPeriod>(`${this.resourceUrl}/${this.getDataInputPeriodIdentifier(dataInputPeriod)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dataInputPeriod: PartialUpdateDataInputPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataInputPeriod);
    return this.http
      .patch<RestDataInputPeriod>(`${this.resourceUrl}/${this.getDataInputPeriodIdentifier(dataInputPeriod)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDataInputPeriod>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDataInputPeriod[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDataInputPeriodIdentifier(dataInputPeriod: Pick<IDataInputPeriod, 'id'>): number {
    return dataInputPeriod.id;
  }

  compareDataInputPeriod(o1: Pick<IDataInputPeriod, 'id'> | null, o2: Pick<IDataInputPeriod, 'id'> | null): boolean {
    return o1 && o2 ? this.getDataInputPeriodIdentifier(o1) === this.getDataInputPeriodIdentifier(o2) : o1 === o2;
  }

  addDataInputPeriodToCollectionIfMissing<Type extends Pick<IDataInputPeriod, 'id'>>(
    dataInputPeriodCollection: Type[],
    ...dataInputPeriodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dataInputPeriods: Type[] = dataInputPeriodsToCheck.filter(isPresent);
    if (dataInputPeriods.length > 0) {
      const dataInputPeriodCollectionIdentifiers = dataInputPeriodCollection.map(
        dataInputPeriodItem => this.getDataInputPeriodIdentifier(dataInputPeriodItem)!
      );
      const dataInputPeriodsToAdd = dataInputPeriods.filter(dataInputPeriodItem => {
        const dataInputPeriodIdentifier = this.getDataInputPeriodIdentifier(dataInputPeriodItem);
        if (dataInputPeriodCollectionIdentifiers.includes(dataInputPeriodIdentifier)) {
          return false;
        }
        dataInputPeriodCollectionIdentifiers.push(dataInputPeriodIdentifier);
        return true;
      });
      return [...dataInputPeriodsToAdd, ...dataInputPeriodCollection];
    }
    return dataInputPeriodCollection;
  }

  protected convertDateFromClient<T extends IDataInputPeriod | NewDataInputPeriod | PartialUpdateDataInputPeriod>(
    dataInputPeriod: T
  ): RestOf<T> {
    return {
      ...dataInputPeriod,
      openingDate: dataInputPeriod.openingDate?.format(DATE_FORMAT) ?? null,
      closingDate: dataInputPeriod.closingDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDataInputPeriod: RestDataInputPeriod): IDataInputPeriod {
    return {
      ...restDataInputPeriod,
      openingDate: restDataInputPeriod.openingDate ? dayjs(restDataInputPeriod.openingDate) : undefined,
      closingDate: restDataInputPeriod.closingDate ? dayjs(restDataInputPeriod.closingDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDataInputPeriod>): HttpResponse<IDataInputPeriod> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDataInputPeriod[]>): HttpResponse<IDataInputPeriod[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
