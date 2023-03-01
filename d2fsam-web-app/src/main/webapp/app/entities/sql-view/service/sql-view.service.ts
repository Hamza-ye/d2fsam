import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISqlView, NewSqlView } from '../sql-view.model';

export type PartialUpdateSqlView = Partial<ISqlView> & Pick<ISqlView, 'id'>;

type RestOf<T extends ISqlView | NewSqlView> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestSqlView = RestOf<ISqlView>;

export type NewRestSqlView = RestOf<NewSqlView>;

export type PartialUpdateRestSqlView = RestOf<PartialUpdateSqlView>;

export type EntityResponseType = HttpResponse<ISqlView>;
export type EntityArrayResponseType = HttpResponse<ISqlView[]>;

@Injectable({ providedIn: 'root' })
export class SqlViewService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sql-views');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(sqlView: NewSqlView): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sqlView);
    return this.http
      .post<RestSqlView>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(sqlView: ISqlView): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sqlView);
    return this.http
      .put<RestSqlView>(`${this.resourceUrl}/${this.getSqlViewIdentifier(sqlView)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(sqlView: PartialUpdateSqlView): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sqlView);
    return this.http
      .patch<RestSqlView>(`${this.resourceUrl}/${this.getSqlViewIdentifier(sqlView)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSqlView>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSqlView[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSqlViewIdentifier(sqlView: Pick<ISqlView, 'id'>): number {
    return sqlView.id;
  }

  compareSqlView(o1: Pick<ISqlView, 'id'> | null, o2: Pick<ISqlView, 'id'> | null): boolean {
    return o1 && o2 ? this.getSqlViewIdentifier(o1) === this.getSqlViewIdentifier(o2) : o1 === o2;
  }

  addSqlViewToCollectionIfMissing<Type extends Pick<ISqlView, 'id'>>(
    sqlViewCollection: Type[],
    ...sqlViewsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sqlViews: Type[] = sqlViewsToCheck.filter(isPresent);
    if (sqlViews.length > 0) {
      const sqlViewCollectionIdentifiers = sqlViewCollection.map(sqlViewItem => this.getSqlViewIdentifier(sqlViewItem)!);
      const sqlViewsToAdd = sqlViews.filter(sqlViewItem => {
        const sqlViewIdentifier = this.getSqlViewIdentifier(sqlViewItem);
        if (sqlViewCollectionIdentifiers.includes(sqlViewIdentifier)) {
          return false;
        }
        sqlViewCollectionIdentifiers.push(sqlViewIdentifier);
        return true;
      });
      return [...sqlViewsToAdd, ...sqlViewCollection];
    }
    return sqlViewCollection;
  }

  protected convertDateFromClient<T extends ISqlView | NewSqlView | PartialUpdateSqlView>(sqlView: T): RestOf<T> {
    return {
      ...sqlView,
      created: sqlView.created?.toJSON() ?? null,
      updated: sqlView.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSqlView: RestSqlView): ISqlView {
    return {
      ...restSqlView,
      created: restSqlView.created ? dayjs(restSqlView.created) : undefined,
      updated: restSqlView.updated ? dayjs(restSqlView.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSqlView>): HttpResponse<ISqlView> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSqlView[]>): HttpResponse<ISqlView[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
