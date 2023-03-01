import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPeriod, NewPeriod } from '../period.model';

export type PartialUpdatePeriod = Partial<IPeriod> & Pick<IPeriod, 'id'>;

type RestOf<T extends IPeriod | NewPeriod> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestPeriod = RestOf<IPeriod>;

export type NewRestPeriod = RestOf<NewPeriod>;

export type PartialUpdateRestPeriod = RestOf<PartialUpdatePeriod>;

export type EntityResponseType = HttpResponse<IPeriod>;
export type EntityArrayResponseType = HttpResponse<IPeriod[]>;

@Injectable({ providedIn: 'root' })
export class PeriodService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/periods');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(period: NewPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(period);
    return this.http
      .post<RestPeriod>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(period: IPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(period);
    return this.http
      .put<RestPeriod>(`${this.resourceUrl}/${this.getPeriodIdentifier(period)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(period: PartialUpdatePeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(period);
    return this.http
      .patch<RestPeriod>(`${this.resourceUrl}/${this.getPeriodIdentifier(period)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPeriod>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPeriod[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPeriodIdentifier(period: Pick<IPeriod, 'id'>): number {
    return period.id;
  }

  comparePeriod(o1: Pick<IPeriod, 'id'> | null, o2: Pick<IPeriod, 'id'> | null): boolean {
    return o1 && o2 ? this.getPeriodIdentifier(o1) === this.getPeriodIdentifier(o2) : o1 === o2;
  }

  addPeriodToCollectionIfMissing<Type extends Pick<IPeriod, 'id'>>(
    periodCollection: Type[],
    ...periodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const periods: Type[] = periodsToCheck.filter(isPresent);
    if (periods.length > 0) {
      const periodCollectionIdentifiers = periodCollection.map(periodItem => this.getPeriodIdentifier(periodItem)!);
      const periodsToAdd = periods.filter(periodItem => {
        const periodIdentifier = this.getPeriodIdentifier(periodItem);
        if (periodCollectionIdentifiers.includes(periodIdentifier)) {
          return false;
        }
        periodCollectionIdentifiers.push(periodIdentifier);
        return true;
      });
      return [...periodsToAdd, ...periodCollection];
    }
    return periodCollection;
  }

  protected convertDateFromClient<T extends IPeriod | NewPeriod | PartialUpdatePeriod>(period: T): RestOf<T> {
    return {
      ...period,
      startDate: period.startDate?.format(DATE_FORMAT) ?? null,
      endDate: period.endDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPeriod: RestPeriod): IPeriod {
    return {
      ...restPeriod,
      startDate: restPeriod.startDate ? dayjs(restPeriod.startDate) : undefined,
      endDate: restPeriod.endDate ? dayjs(restPeriod.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPeriod>): HttpResponse<IPeriod> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPeriod[]>): HttpResponse<IPeriod[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
