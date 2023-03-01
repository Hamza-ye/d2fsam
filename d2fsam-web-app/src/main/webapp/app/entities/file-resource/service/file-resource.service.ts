import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFileResource, NewFileResource } from '../file-resource.model';

export type PartialUpdateFileResource = Partial<IFileResource> & Pick<IFileResource, 'id'>;

type RestOf<T extends IFileResource | NewFileResource> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestFileResource = RestOf<IFileResource>;

export type NewRestFileResource = RestOf<NewFileResource>;

export type PartialUpdateRestFileResource = RestOf<PartialUpdateFileResource>;

export type EntityResponseType = HttpResponse<IFileResource>;
export type EntityArrayResponseType = HttpResponse<IFileResource[]>;

@Injectable({ providedIn: 'root' })
export class FileResourceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/file-resources');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(fileResource: NewFileResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fileResource);
    return this.http
      .post<RestFileResource>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(fileResource: IFileResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fileResource);
    return this.http
      .put<RestFileResource>(`${this.resourceUrl}/${this.getFileResourceIdentifier(fileResource)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(fileResource: PartialUpdateFileResource): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(fileResource);
    return this.http
      .patch<RestFileResource>(`${this.resourceUrl}/${this.getFileResourceIdentifier(fileResource)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFileResource>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFileResource[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFileResourceIdentifier(fileResource: Pick<IFileResource, 'id'>): number {
    return fileResource.id;
  }

  compareFileResource(o1: Pick<IFileResource, 'id'> | null, o2: Pick<IFileResource, 'id'> | null): boolean {
    return o1 && o2 ? this.getFileResourceIdentifier(o1) === this.getFileResourceIdentifier(o2) : o1 === o2;
  }

  addFileResourceToCollectionIfMissing<Type extends Pick<IFileResource, 'id'>>(
    fileResourceCollection: Type[],
    ...fileResourcesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const fileResources: Type[] = fileResourcesToCheck.filter(isPresent);
    if (fileResources.length > 0) {
      const fileResourceCollectionIdentifiers = fileResourceCollection.map(
        fileResourceItem => this.getFileResourceIdentifier(fileResourceItem)!
      );
      const fileResourcesToAdd = fileResources.filter(fileResourceItem => {
        const fileResourceIdentifier = this.getFileResourceIdentifier(fileResourceItem);
        if (fileResourceCollectionIdentifiers.includes(fileResourceIdentifier)) {
          return false;
        }
        fileResourceCollectionIdentifiers.push(fileResourceIdentifier);
        return true;
      });
      return [...fileResourcesToAdd, ...fileResourceCollection];
    }
    return fileResourceCollection;
  }

  protected convertDateFromClient<T extends IFileResource | NewFileResource | PartialUpdateFileResource>(fileResource: T): RestOf<T> {
    return {
      ...fileResource,
      created: fileResource.created?.toJSON() ?? null,
      updated: fileResource.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFileResource: RestFileResource): IFileResource {
    return {
      ...restFileResource,
      created: restFileResource.created ? dayjs(restFileResource.created) : undefined,
      updated: restFileResource.updated ? dayjs(restFileResource.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFileResource>): HttpResponse<IFileResource> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFileResource[]>): HttpResponse<IFileResource[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
