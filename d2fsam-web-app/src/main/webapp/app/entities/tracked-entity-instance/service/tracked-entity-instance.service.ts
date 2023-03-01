import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityInstance, NewTrackedEntityInstance } from '../tracked-entity-instance.model';

export type PartialUpdateTrackedEntityInstance = Partial<ITrackedEntityInstance> & Pick<ITrackedEntityInstance, 'id'>;

type RestOf<T extends ITrackedEntityInstance | NewTrackedEntityInstance> = Omit<
  T,
  'created' | 'updated' | 'createdAtClient' | 'updatedAtClient' | 'lastSynchronized'
> & {
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  lastSynchronized?: string | null;
};

export type RestTrackedEntityInstance = RestOf<ITrackedEntityInstance>;

export type NewRestTrackedEntityInstance = RestOf<NewTrackedEntityInstance>;

export type PartialUpdateRestTrackedEntityInstance = RestOf<PartialUpdateTrackedEntityInstance>;

export type EntityResponseType = HttpResponse<ITrackedEntityInstance>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityInstance[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-instances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityInstance: NewTrackedEntityInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstance);
    return this.http
      .post<RestTrackedEntityInstance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityInstance: ITrackedEntityInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstance);
    return this.http
      .put<RestTrackedEntityInstance>(`${this.resourceUrl}/${this.getTrackedEntityInstanceIdentifier(trackedEntityInstance)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityInstance: PartialUpdateTrackedEntityInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstance);
    return this.http
      .patch<RestTrackedEntityInstance>(`${this.resourceUrl}/${this.getTrackedEntityInstanceIdentifier(trackedEntityInstance)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityInstance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityInstance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityInstanceIdentifier(trackedEntityInstance: Pick<ITrackedEntityInstance, 'id'>): number {
    return trackedEntityInstance.id;
  }

  compareTrackedEntityInstance(o1: Pick<ITrackedEntityInstance, 'id'> | null, o2: Pick<ITrackedEntityInstance, 'id'> | null): boolean {
    return o1 && o2 ? this.getTrackedEntityInstanceIdentifier(o1) === this.getTrackedEntityInstanceIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityInstanceToCollectionIfMissing<Type extends Pick<ITrackedEntityInstance, 'id'>>(
    trackedEntityInstanceCollection: Type[],
    ...trackedEntityInstancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityInstances: Type[] = trackedEntityInstancesToCheck.filter(isPresent);
    if (trackedEntityInstances.length > 0) {
      const trackedEntityInstanceCollectionIdentifiers = trackedEntityInstanceCollection.map(
        trackedEntityInstanceItem => this.getTrackedEntityInstanceIdentifier(trackedEntityInstanceItem)!
      );
      const trackedEntityInstancesToAdd = trackedEntityInstances.filter(trackedEntityInstanceItem => {
        const trackedEntityInstanceIdentifier = this.getTrackedEntityInstanceIdentifier(trackedEntityInstanceItem);
        if (trackedEntityInstanceCollectionIdentifiers.includes(trackedEntityInstanceIdentifier)) {
          return false;
        }
        trackedEntityInstanceCollectionIdentifiers.push(trackedEntityInstanceIdentifier);
        return true;
      });
      return [...trackedEntityInstancesToAdd, ...trackedEntityInstanceCollection];
    }
    return trackedEntityInstanceCollection;
  }

  protected convertDateFromClient<T extends ITrackedEntityInstance | NewTrackedEntityInstance | PartialUpdateTrackedEntityInstance>(
    trackedEntityInstance: T
  ): RestOf<T> {
    return {
      ...trackedEntityInstance,
      created: trackedEntityInstance.created?.toJSON() ?? null,
      updated: trackedEntityInstance.updated?.toJSON() ?? null,
      createdAtClient: trackedEntityInstance.createdAtClient?.toJSON() ?? null,
      updatedAtClient: trackedEntityInstance.updatedAtClient?.toJSON() ?? null,
      lastSynchronized: trackedEntityInstance.lastSynchronized?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityInstance: RestTrackedEntityInstance): ITrackedEntityInstance {
    return {
      ...restTrackedEntityInstance,
      created: restTrackedEntityInstance.created ? dayjs(restTrackedEntityInstance.created) : undefined,
      updated: restTrackedEntityInstance.updated ? dayjs(restTrackedEntityInstance.updated) : undefined,
      createdAtClient: restTrackedEntityInstance.createdAtClient ? dayjs(restTrackedEntityInstance.createdAtClient) : undefined,
      updatedAtClient: restTrackedEntityInstance.updatedAtClient ? dayjs(restTrackedEntityInstance.updatedAtClient) : undefined,
      lastSynchronized: restTrackedEntityInstance.lastSynchronized ? dayjs(restTrackedEntityInstance.lastSynchronized) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityInstance>): HttpResponse<ITrackedEntityInstance> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTrackedEntityInstance[]>): HttpResponse<ITrackedEntityInstance[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
