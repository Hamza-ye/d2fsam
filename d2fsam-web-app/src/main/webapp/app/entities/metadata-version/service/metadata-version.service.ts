import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMetadataVersion, NewMetadataVersion } from '../metadata-version.model';

export type PartialUpdateMetadataVersion = Partial<IMetadataVersion> & Pick<IMetadataVersion, 'id'>;

type RestOf<T extends IMetadataVersion | NewMetadataVersion> = Omit<T, 'created' | 'updated' | 'importDate'> & {
  created?: string | null;
  updated?: string | null;
  importDate?: string | null;
};

export type RestMetadataVersion = RestOf<IMetadataVersion>;

export type NewRestMetadataVersion = RestOf<NewMetadataVersion>;

export type PartialUpdateRestMetadataVersion = RestOf<PartialUpdateMetadataVersion>;

export type EntityResponseType = HttpResponse<IMetadataVersion>;
export type EntityArrayResponseType = HttpResponse<IMetadataVersion[]>;

@Injectable({ providedIn: 'root' })
export class MetadataVersionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/metadata-versions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(metadataVersion: NewMetadataVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(metadataVersion);
    return this.http
      .post<RestMetadataVersion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(metadataVersion: IMetadataVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(metadataVersion);
    return this.http
      .put<RestMetadataVersion>(`${this.resourceUrl}/${this.getMetadataVersionIdentifier(metadataVersion)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(metadataVersion: PartialUpdateMetadataVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(metadataVersion);
    return this.http
      .patch<RestMetadataVersion>(`${this.resourceUrl}/${this.getMetadataVersionIdentifier(metadataVersion)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMetadataVersion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMetadataVersion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMetadataVersionIdentifier(metadataVersion: Pick<IMetadataVersion, 'id'>): number {
    return metadataVersion.id;
  }

  compareMetadataVersion(o1: Pick<IMetadataVersion, 'id'> | null, o2: Pick<IMetadataVersion, 'id'> | null): boolean {
    return o1 && o2 ? this.getMetadataVersionIdentifier(o1) === this.getMetadataVersionIdentifier(o2) : o1 === o2;
  }

  addMetadataVersionToCollectionIfMissing<Type extends Pick<IMetadataVersion, 'id'>>(
    metadataVersionCollection: Type[],
    ...metadataVersionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const metadataVersions: Type[] = metadataVersionsToCheck.filter(isPresent);
    if (metadataVersions.length > 0) {
      const metadataVersionCollectionIdentifiers = metadataVersionCollection.map(
        metadataVersionItem => this.getMetadataVersionIdentifier(metadataVersionItem)!
      );
      const metadataVersionsToAdd = metadataVersions.filter(metadataVersionItem => {
        const metadataVersionIdentifier = this.getMetadataVersionIdentifier(metadataVersionItem);
        if (metadataVersionCollectionIdentifiers.includes(metadataVersionIdentifier)) {
          return false;
        }
        metadataVersionCollectionIdentifiers.push(metadataVersionIdentifier);
        return true;
      });
      return [...metadataVersionsToAdd, ...metadataVersionCollection];
    }
    return metadataVersionCollection;
  }

  protected convertDateFromClient<T extends IMetadataVersion | NewMetadataVersion | PartialUpdateMetadataVersion>(
    metadataVersion: T
  ): RestOf<T> {
    return {
      ...metadataVersion,
      created: metadataVersion.created?.toJSON() ?? null,
      updated: metadataVersion.updated?.toJSON() ?? null,
      importDate: metadataVersion.importDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMetadataVersion: RestMetadataVersion): IMetadataVersion {
    return {
      ...restMetadataVersion,
      created: restMetadataVersion.created ? dayjs(restMetadataVersion.created) : undefined,
      updated: restMetadataVersion.updated ? dayjs(restMetadataVersion.updated) : undefined,
      importDate: restMetadataVersion.importDate ? dayjs(restMetadataVersion.importDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMetadataVersion>): HttpResponse<IMetadataVersion> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMetadataVersion[]>): HttpResponse<IMetadataVersion[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
