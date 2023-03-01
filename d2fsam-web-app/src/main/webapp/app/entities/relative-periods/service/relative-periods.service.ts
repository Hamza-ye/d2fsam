import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRelativePeriods, NewRelativePeriods } from '../relative-periods.model';

export type PartialUpdateRelativePeriods = Partial<IRelativePeriods> & Pick<IRelativePeriods, 'id'>;

export type EntityResponseType = HttpResponse<IRelativePeriods>;
export type EntityArrayResponseType = HttpResponse<IRelativePeriods[]>;

@Injectable({ providedIn: 'root' })
export class RelativePeriodsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/relative-periods');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(relativePeriods: NewRelativePeriods): Observable<EntityResponseType> {
    return this.http.post<IRelativePeriods>(this.resourceUrl, relativePeriods, { observe: 'response' });
  }

  update(relativePeriods: IRelativePeriods): Observable<EntityResponseType> {
    return this.http.put<IRelativePeriods>(`${this.resourceUrl}/${this.getRelativePeriodsIdentifier(relativePeriods)}`, relativePeriods, {
      observe: 'response',
    });
  }

  partialUpdate(relativePeriods: PartialUpdateRelativePeriods): Observable<EntityResponseType> {
    return this.http.patch<IRelativePeriods>(`${this.resourceUrl}/${this.getRelativePeriodsIdentifier(relativePeriods)}`, relativePeriods, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRelativePeriods>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelativePeriods[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRelativePeriodsIdentifier(relativePeriods: Pick<IRelativePeriods, 'id'>): number {
    return relativePeriods.id;
  }

  compareRelativePeriods(o1: Pick<IRelativePeriods, 'id'> | null, o2: Pick<IRelativePeriods, 'id'> | null): boolean {
    return o1 && o2 ? this.getRelativePeriodsIdentifier(o1) === this.getRelativePeriodsIdentifier(o2) : o1 === o2;
  }

  addRelativePeriodsToCollectionIfMissing<Type extends Pick<IRelativePeriods, 'id'>>(
    relativePeriodsCollection: Type[],
    ...relativePeriodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const relativePeriods: Type[] = relativePeriodsToCheck.filter(isPresent);
    if (relativePeriods.length > 0) {
      const relativePeriodsCollectionIdentifiers = relativePeriodsCollection.map(
        relativePeriodsItem => this.getRelativePeriodsIdentifier(relativePeriodsItem)!
      );
      const relativePeriodsToAdd = relativePeriods.filter(relativePeriodsItem => {
        const relativePeriodsIdentifier = this.getRelativePeriodsIdentifier(relativePeriodsItem);
        if (relativePeriodsCollectionIdentifiers.includes(relativePeriodsIdentifier)) {
          return false;
        }
        relativePeriodsCollectionIdentifiers.push(relativePeriodsIdentifier);
        return true;
      });
      return [...relativePeriodsToAdd, ...relativePeriodsCollection];
    }
    return relativePeriodsCollection;
  }
}
