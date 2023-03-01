import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStockItemGroup, NewStockItemGroup } from '../stock-item-group.model';

export type PartialUpdateStockItemGroup = Partial<IStockItemGroup> & Pick<IStockItemGroup, 'id'>;

type RestOf<T extends IStockItemGroup | NewStockItemGroup> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestStockItemGroup = RestOf<IStockItemGroup>;

export type NewRestStockItemGroup = RestOf<NewStockItemGroup>;

export type PartialUpdateRestStockItemGroup = RestOf<PartialUpdateStockItemGroup>;

export type EntityResponseType = HttpResponse<IStockItemGroup>;
export type EntityArrayResponseType = HttpResponse<IStockItemGroup[]>;

@Injectable({ providedIn: 'root' })
export class StockItemGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/stock-item-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(stockItemGroup: NewStockItemGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockItemGroup);
    return this.http
      .post<RestStockItemGroup>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stockItemGroup: IStockItemGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockItemGroup);
    return this.http
      .put<RestStockItemGroup>(`${this.resourceUrl}/${this.getStockItemGroupIdentifier(stockItemGroup)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stockItemGroup: PartialUpdateStockItemGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockItemGroup);
    return this.http
      .patch<RestStockItemGroup>(`${this.resourceUrl}/${this.getStockItemGroupIdentifier(stockItemGroup)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStockItemGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockItemGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStockItemGroupIdentifier(stockItemGroup: Pick<IStockItemGroup, 'id'>): number {
    return stockItemGroup.id;
  }

  compareStockItemGroup(o1: Pick<IStockItemGroup, 'id'> | null, o2: Pick<IStockItemGroup, 'id'> | null): boolean {
    return o1 && o2 ? this.getStockItemGroupIdentifier(o1) === this.getStockItemGroupIdentifier(o2) : o1 === o2;
  }

  addStockItemGroupToCollectionIfMissing<Type extends Pick<IStockItemGroup, 'id'>>(
    stockItemGroupCollection: Type[],
    ...stockItemGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stockItemGroups: Type[] = stockItemGroupsToCheck.filter(isPresent);
    if (stockItemGroups.length > 0) {
      const stockItemGroupCollectionIdentifiers = stockItemGroupCollection.map(
        stockItemGroupItem => this.getStockItemGroupIdentifier(stockItemGroupItem)!
      );
      const stockItemGroupsToAdd = stockItemGroups.filter(stockItemGroupItem => {
        const stockItemGroupIdentifier = this.getStockItemGroupIdentifier(stockItemGroupItem);
        if (stockItemGroupCollectionIdentifiers.includes(stockItemGroupIdentifier)) {
          return false;
        }
        stockItemGroupCollectionIdentifiers.push(stockItemGroupIdentifier);
        return true;
      });
      return [...stockItemGroupsToAdd, ...stockItemGroupCollection];
    }
    return stockItemGroupCollection;
  }

  protected convertDateFromClient<T extends IStockItemGroup | NewStockItemGroup | PartialUpdateStockItemGroup>(
    stockItemGroup: T
  ): RestOf<T> {
    return {
      ...stockItemGroup,
      created: stockItemGroup.created?.toJSON() ?? null,
      updated: stockItemGroup.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restStockItemGroup: RestStockItemGroup): IStockItemGroup {
    return {
      ...restStockItemGroup,
      created: restStockItemGroup.created ? dayjs(restStockItemGroup.created) : undefined,
      updated: restStockItemGroup.updated ? dayjs(restStockItemGroup.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStockItemGroup>): HttpResponse<IStockItemGroup> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStockItemGroup[]>): HttpResponse<IStockItemGroup[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
