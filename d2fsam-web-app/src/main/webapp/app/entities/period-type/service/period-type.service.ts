import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPeriodType, NewPeriodType } from '../period-type.model';

export type PartialUpdatePeriodType = Partial<IPeriodType> & Pick<IPeriodType, 'id'>;

export type EntityResponseType = HttpResponse<IPeriodType>;
export type EntityArrayResponseType = HttpResponse<IPeriodType[]>;

@Injectable({ providedIn: 'root' })
export class PeriodTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/period-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(periodType: NewPeriodType): Observable<EntityResponseType> {
    return this.http.post<IPeriodType>(this.resourceUrl, periodType, { observe: 'response' });
  }

  update(periodType: IPeriodType): Observable<EntityResponseType> {
    return this.http.put<IPeriodType>(`${this.resourceUrl}/${this.getPeriodTypeIdentifier(periodType)}`, periodType, {
      observe: 'response',
    });
  }

  partialUpdate(periodType: PartialUpdatePeriodType): Observable<EntityResponseType> {
    return this.http.patch<IPeriodType>(`${this.resourceUrl}/${this.getPeriodTypeIdentifier(periodType)}`, periodType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPeriodType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPeriodType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPeriodTypeIdentifier(periodType: Pick<IPeriodType, 'id'>): number {
    return periodType.id;
  }

  comparePeriodType(o1: Pick<IPeriodType, 'id'> | null, o2: Pick<IPeriodType, 'id'> | null): boolean {
    return o1 && o2 ? this.getPeriodTypeIdentifier(o1) === this.getPeriodTypeIdentifier(o2) : o1 === o2;
  }

  addPeriodTypeToCollectionIfMissing<Type extends Pick<IPeriodType, 'id'>>(
    periodTypeCollection: Type[],
    ...periodTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const periodTypes: Type[] = periodTypesToCheck.filter(isPresent);
    if (periodTypes.length > 0) {
      const periodTypeCollectionIdentifiers = periodTypeCollection.map(periodTypeItem => this.getPeriodTypeIdentifier(periodTypeItem)!);
      const periodTypesToAdd = periodTypes.filter(periodTypeItem => {
        const periodTypeIdentifier = this.getPeriodTypeIdentifier(periodTypeItem);
        if (periodTypeCollectionIdentifiers.includes(periodTypeIdentifier)) {
          return false;
        }
        periodTypeCollectionIdentifiers.push(periodTypeIdentifier);
        return true;
      });
      return [...periodTypesToAdd, ...periodTypeCollection];
    }
    return periodTypeCollection;
  }
}
