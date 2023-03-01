import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChv, NewChv } from '../chv.model';

export type PartialUpdateChv = Partial<IChv> & Pick<IChv, 'id'>;

type RestOf<T extends IChv | NewChv> = Omit<T, 'created' | 'updated' | 'dateJoined' | 'dateWithdrawn'> & {
  created?: string | null;
  updated?: string | null;
  dateJoined?: string | null;
  dateWithdrawn?: string | null;
};

export type RestChv = RestOf<IChv>;

export type NewRestChv = RestOf<NewChv>;

export type PartialUpdateRestChv = RestOf<PartialUpdateChv>;

export type EntityResponseType = HttpResponse<IChv>;
export type EntityArrayResponseType = HttpResponse<IChv[]>;

@Injectable({ providedIn: 'root' })
export class ChvService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/chvs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(chv: NewChv): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(chv);
    return this.http.post<RestChv>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(chv: IChv): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(chv);
    return this.http
      .put<RestChv>(`${this.resourceUrl}/${this.getChvIdentifier(chv)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(chv: PartialUpdateChv): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(chv);
    return this.http
      .patch<RestChv>(`${this.resourceUrl}/${this.getChvIdentifier(chv)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestChv>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestChv[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getChvIdentifier(chv: Pick<IChv, 'id'>): number {
    return chv.id;
  }

  compareChv(o1: Pick<IChv, 'id'> | null, o2: Pick<IChv, 'id'> | null): boolean {
    return o1 && o2 ? this.getChvIdentifier(o1) === this.getChvIdentifier(o2) : o1 === o2;
  }

  addChvToCollectionIfMissing<Type extends Pick<IChv, 'id'>>(chvCollection: Type[], ...chvsToCheck: (Type | null | undefined)[]): Type[] {
    const chvs: Type[] = chvsToCheck.filter(isPresent);
    if (chvs.length > 0) {
      const chvCollectionIdentifiers = chvCollection.map(chvItem => this.getChvIdentifier(chvItem)!);
      const chvsToAdd = chvs.filter(chvItem => {
        const chvIdentifier = this.getChvIdentifier(chvItem);
        if (chvCollectionIdentifiers.includes(chvIdentifier)) {
          return false;
        }
        chvCollectionIdentifiers.push(chvIdentifier);
        return true;
      });
      return [...chvsToAdd, ...chvCollection];
    }
    return chvCollection;
  }

  protected convertDateFromClient<T extends IChv | NewChv | PartialUpdateChv>(chv: T): RestOf<T> {
    return {
      ...chv,
      created: chv.created?.toJSON() ?? null,
      updated: chv.updated?.toJSON() ?? null,
      dateJoined: chv.dateJoined?.format(DATE_FORMAT) ?? null,
      dateWithdrawn: chv.dateWithdrawn?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restChv: RestChv): IChv {
    return {
      ...restChv,
      created: restChv.created ? dayjs(restChv.created) : undefined,
      updated: restChv.updated ? dayjs(restChv.updated) : undefined,
      dateJoined: restChv.dateJoined ? dayjs(restChv.dateJoined) : undefined,
      dateWithdrawn: restChv.dateWithdrawn ? dayjs(restChv.dateWithdrawn) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestChv>): HttpResponse<IChv> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestChv[]>): HttpResponse<IChv[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
