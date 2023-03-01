import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExternalFileResource, NewExternalFileResource } from '../external-file-resource.model';

export type PartialUpdateExternalFileResource = Partial<IExternalFileResource> & Pick<IExternalFileResource, 'id'>;

type RestOf<T extends IExternalFileResource | NewExternalFileResource> = Omit<T, 'created' | 'updated' | 'expires'> & {
  created?: string | null;
  updated?: string | null;
  expires?: string | null;
};

export type RestExternalFileResource = RestOf<IExternalFileResource>;

export type NewRestExternalFileResource = RestOf<NewExternalFileResource>;

export type PartialUpdateRestExternalFileResource = RestOf<PartialUpdateExternalFileResource>;

export type EntityResponseType = HttpResponse<IExternalFileResource>;
export type EntityArrayResponseType = HttpResponse<IExternalFileResource[]>;

@Injectable({ providedIn: 'root' })
export class ExternalFileResourceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/external-file-resources');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(externalFileResource: NewExternalFileResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(externalFileResource);
    return this.http
      .post<RestExternalFileResource>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(externalFileResource: IExternalFileResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(externalFileResource);
    return this.http
      .put<RestExternalFileResource>(`${this.resourceUrl}/${this.getExternalFileResourceIdentifier(externalFileResource)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(externalFileResource: PartialUpdateExternalFileResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(externalFileResource);
    return this.http
      .patch<RestExternalFileResource>(`${this.resourceUrl}/${this.getExternalFileResourceIdentifier(externalFileResource)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestExternalFileResource>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExternalFileResource[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getExternalFileResourceIdentifier(externalFileResource: Pick<IExternalFileResource, 'id'>): number {
    return externalFileResource.id;
  }

  compareExternalFileResource(o1: Pick<IExternalFileResource, 'id'> | null, o2: Pick<IExternalFileResource, 'id'> | null): boolean {
    return o1 && o2 ? this.getExternalFileResourceIdentifier(o1) === this.getExternalFileResourceIdentifier(o2) : o1 === o2;
  }

  addExternalFileResourceToCollectionIfMissing<Type extends Pick<IExternalFileResource, 'id'>>(
    externalFileResourceCollection: Type[],
    ...externalFileResourcesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const externalFileResources: Type[] = externalFileResourcesToCheck.filter(isPresent);
    if (externalFileResources.length > 0) {
      const externalFileResourceCollectionIdentifiers = externalFileResourceCollection.map(
        externalFileResourceItem => this.getExternalFileResourceIdentifier(externalFileResourceItem)!
      );
      const externalFileResourcesToAdd = externalFileResources.filter(externalFileResourceItem => {
        const externalFileResourceIdentifier = this.getExternalFileResourceIdentifier(externalFileResourceItem);
        if (externalFileResourceCollectionIdentifiers.includes(externalFileResourceIdentifier)) {
          return false;
        }
        externalFileResourceCollectionIdentifiers.push(externalFileResourceIdentifier);
        return true;
      });
      return [...externalFileResourcesToAdd, ...externalFileResourceCollection];
    }
    return externalFileResourceCollection;
  }

  protected convertDateFromClient<T extends IExternalFileResource | NewExternalFileResource | PartialUpdateExternalFileResource>(
    externalFileResource: T
  ): RestOf<T> {
    return {
      ...externalFileResource,
      created: externalFileResource.created?.toJSON() ?? null,
      updated: externalFileResource.updated?.toJSON() ?? null,
      expires: externalFileResource.expires?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restExternalFileResource: RestExternalFileResource): IExternalFileResource {
    return {
      ...restExternalFileResource,
      created: restExternalFileResource.created ? dayjs(restExternalFileResource.created) : undefined,
      updated: restExternalFileResource.updated ? dayjs(restExternalFileResource.updated) : undefined,
      expires: restExternalFileResource.expires ? dayjs(restExternalFileResource.expires) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestExternalFileResource>): HttpResponse<IExternalFileResource> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestExternalFileResource[]>): HttpResponse<IExternalFileResource[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
