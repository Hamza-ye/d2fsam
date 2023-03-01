import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrganisationUnitLevel, NewOrganisationUnitLevel } from '../organisation-unit-level.model';

export type PartialUpdateOrganisationUnitLevel = Partial<IOrganisationUnitLevel> & Pick<IOrganisationUnitLevel, 'id'>;

type RestOf<T extends IOrganisationUnitLevel | NewOrganisationUnitLevel> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestOrganisationUnitLevel = RestOf<IOrganisationUnitLevel>;

export type NewRestOrganisationUnitLevel = RestOf<NewOrganisationUnitLevel>;

export type PartialUpdateRestOrganisationUnitLevel = RestOf<PartialUpdateOrganisationUnitLevel>;

export type EntityResponseType = HttpResponse<IOrganisationUnitLevel>;
export type EntityArrayResponseType = HttpResponse<IOrganisationUnitLevel[]>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitLevelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/organisation-unit-levels');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(organisationUnitLevel: NewOrganisationUnitLevel): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitLevel);
    return this.http
      .post<RestOrganisationUnitLevel>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(organisationUnitLevel: IOrganisationUnitLevel): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitLevel);
    return this.http
      .put<RestOrganisationUnitLevel>(`${this.resourceUrl}/${this.getOrganisationUnitLevelIdentifier(organisationUnitLevel)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(organisationUnitLevel: PartialUpdateOrganisationUnitLevel): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitLevel);
    return this.http
      .patch<RestOrganisationUnitLevel>(`${this.resourceUrl}/${this.getOrganisationUnitLevelIdentifier(organisationUnitLevel)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrganisationUnitLevel>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrganisationUnitLevel[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOrganisationUnitLevelIdentifier(organisationUnitLevel: Pick<IOrganisationUnitLevel, 'id'>): number {
    return organisationUnitLevel.id;
  }

  compareOrganisationUnitLevel(o1: Pick<IOrganisationUnitLevel, 'id'> | null, o2: Pick<IOrganisationUnitLevel, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrganisationUnitLevelIdentifier(o1) === this.getOrganisationUnitLevelIdentifier(o2) : o1 === o2;
  }

  addOrganisationUnitLevelToCollectionIfMissing<Type extends Pick<IOrganisationUnitLevel, 'id'>>(
    organisationUnitLevelCollection: Type[],
    ...organisationUnitLevelsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const organisationUnitLevels: Type[] = organisationUnitLevelsToCheck.filter(isPresent);
    if (organisationUnitLevels.length > 0) {
      const organisationUnitLevelCollectionIdentifiers = organisationUnitLevelCollection.map(
        organisationUnitLevelItem => this.getOrganisationUnitLevelIdentifier(organisationUnitLevelItem)!
      );
      const organisationUnitLevelsToAdd = organisationUnitLevels.filter(organisationUnitLevelItem => {
        const organisationUnitLevelIdentifier = this.getOrganisationUnitLevelIdentifier(organisationUnitLevelItem);
        if (organisationUnitLevelCollectionIdentifiers.includes(organisationUnitLevelIdentifier)) {
          return false;
        }
        organisationUnitLevelCollectionIdentifiers.push(organisationUnitLevelIdentifier);
        return true;
      });
      return [...organisationUnitLevelsToAdd, ...organisationUnitLevelCollection];
    }
    return organisationUnitLevelCollection;
  }

  protected convertDateFromClient<T extends IOrganisationUnitLevel | NewOrganisationUnitLevel | PartialUpdateOrganisationUnitLevel>(
    organisationUnitLevel: T
  ): RestOf<T> {
    return {
      ...organisationUnitLevel,
      created: organisationUnitLevel.created?.toJSON() ?? null,
      updated: organisationUnitLevel.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOrganisationUnitLevel: RestOrganisationUnitLevel): IOrganisationUnitLevel {
    return {
      ...restOrganisationUnitLevel,
      created: restOrganisationUnitLevel.created ? dayjs(restOrganisationUnitLevel.created) : undefined,
      updated: restOrganisationUnitLevel.updated ? dayjs(restOrganisationUnitLevel.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrganisationUnitLevel>): HttpResponse<IOrganisationUnitLevel> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrganisationUnitLevel[]>): HttpResponse<IOrganisationUnitLevel[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
