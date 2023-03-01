import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityProgramOwner, NewTrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';

export type PartialUpdateTrackedEntityProgramOwner = Partial<ITrackedEntityProgramOwner> & Pick<ITrackedEntityProgramOwner, 'id'>;

type RestOf<T extends ITrackedEntityProgramOwner | NewTrackedEntityProgramOwner> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityProgramOwner = RestOf<ITrackedEntityProgramOwner>;

export type NewRestTrackedEntityProgramOwner = RestOf<NewTrackedEntityProgramOwner>;

export type PartialUpdateRestTrackedEntityProgramOwner = RestOf<PartialUpdateTrackedEntityProgramOwner>;

export type EntityResponseType = HttpResponse<ITrackedEntityProgramOwner>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityProgramOwner[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityProgramOwnerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-program-owners');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityProgramOwner: NewTrackedEntityProgramOwner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityProgramOwner);
    return this.http
      .post<RestTrackedEntityProgramOwner>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityProgramOwner: ITrackedEntityProgramOwner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityProgramOwner);
    return this.http
      .put<RestTrackedEntityProgramOwner>(
        `${this.resourceUrl}/${this.getTrackedEntityProgramOwnerIdentifier(trackedEntityProgramOwner)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityProgramOwner: PartialUpdateTrackedEntityProgramOwner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityProgramOwner);
    return this.http
      .patch<RestTrackedEntityProgramOwner>(
        `${this.resourceUrl}/${this.getTrackedEntityProgramOwnerIdentifier(trackedEntityProgramOwner)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityProgramOwner>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityProgramOwner[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityProgramOwnerIdentifier(trackedEntityProgramOwner: Pick<ITrackedEntityProgramOwner, 'id'>): number {
    return trackedEntityProgramOwner.id;
  }

  compareTrackedEntityProgramOwner(
    o1: Pick<ITrackedEntityProgramOwner, 'id'> | null,
    o2: Pick<ITrackedEntityProgramOwner, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTrackedEntityProgramOwnerIdentifier(o1) === this.getTrackedEntityProgramOwnerIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityProgramOwnerToCollectionIfMissing<Type extends Pick<ITrackedEntityProgramOwner, 'id'>>(
    trackedEntityProgramOwnerCollection: Type[],
    ...trackedEntityProgramOwnersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityProgramOwners: Type[] = trackedEntityProgramOwnersToCheck.filter(isPresent);
    if (trackedEntityProgramOwners.length > 0) {
      const trackedEntityProgramOwnerCollectionIdentifiers = trackedEntityProgramOwnerCollection.map(
        trackedEntityProgramOwnerItem => this.getTrackedEntityProgramOwnerIdentifier(trackedEntityProgramOwnerItem)!
      );
      const trackedEntityProgramOwnersToAdd = trackedEntityProgramOwners.filter(trackedEntityProgramOwnerItem => {
        const trackedEntityProgramOwnerIdentifier = this.getTrackedEntityProgramOwnerIdentifier(trackedEntityProgramOwnerItem);
        if (trackedEntityProgramOwnerCollectionIdentifiers.includes(trackedEntityProgramOwnerIdentifier)) {
          return false;
        }
        trackedEntityProgramOwnerCollectionIdentifiers.push(trackedEntityProgramOwnerIdentifier);
        return true;
      });
      return [...trackedEntityProgramOwnersToAdd, ...trackedEntityProgramOwnerCollection];
    }
    return trackedEntityProgramOwnerCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityProgramOwner | NewTrackedEntityProgramOwner | PartialUpdateTrackedEntityProgramOwner
  >(trackedEntityProgramOwner: T): RestOf<T> {
    return {
      ...trackedEntityProgramOwner,
      created: trackedEntityProgramOwner.created?.toJSON() ?? null,
      updated: trackedEntityProgramOwner.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityProgramOwner: RestTrackedEntityProgramOwner): ITrackedEntityProgramOwner {
    return {
      ...restTrackedEntityProgramOwner,
      created: restTrackedEntityProgramOwner.created ? dayjs(restTrackedEntityProgramOwner.created) : undefined,
      updated: restTrackedEntityProgramOwner.updated ? dayjs(restTrackedEntityProgramOwner.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityProgramOwner>): HttpResponse<ITrackedEntityProgramOwner> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTrackedEntityProgramOwner[]>): HttpResponse<ITrackedEntityProgramOwner[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
