import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrganisationUnitGroup, NewOrganisationUnitGroup } from '../organisation-unit-group.model';

export type PartialUpdateOrganisationUnitGroup = Partial<IOrganisationUnitGroup> & Pick<IOrganisationUnitGroup, 'id'>;

type RestOf<T extends IOrganisationUnitGroup | NewOrganisationUnitGroup> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestOrganisationUnitGroup = RestOf<IOrganisationUnitGroup>;

export type NewRestOrganisationUnitGroup = RestOf<NewOrganisationUnitGroup>;

export type PartialUpdateRestOrganisationUnitGroup = RestOf<PartialUpdateOrganisationUnitGroup>;

export type EntityResponseType = HttpResponse<IOrganisationUnitGroup>;
export type EntityArrayResponseType = HttpResponse<IOrganisationUnitGroup[]>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/organisation-unit-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(organisationUnitGroup: NewOrganisationUnitGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitGroup);
    return this.http
      .post<RestOrganisationUnitGroup>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(organisationUnitGroup: IOrganisationUnitGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitGroup);
    return this.http
      .put<RestOrganisationUnitGroup>(`${this.resourceUrl}/${this.getOrganisationUnitGroupIdentifier(organisationUnitGroup)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(organisationUnitGroup: PartialUpdateOrganisationUnitGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitGroup);
    return this.http
      .patch<RestOrganisationUnitGroup>(`${this.resourceUrl}/${this.getOrganisationUnitGroupIdentifier(organisationUnitGroup)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrganisationUnitGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrganisationUnitGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOrganisationUnitGroupIdentifier(organisationUnitGroup: Pick<IOrganisationUnitGroup, 'id'>): number {
    return organisationUnitGroup.id;
  }

  compareOrganisationUnitGroup(o1: Pick<IOrganisationUnitGroup, 'id'> | null, o2: Pick<IOrganisationUnitGroup, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrganisationUnitGroupIdentifier(o1) === this.getOrganisationUnitGroupIdentifier(o2) : o1 === o2;
  }

  addOrganisationUnitGroupToCollectionIfMissing<Type extends Pick<IOrganisationUnitGroup, 'id'>>(
    organisationUnitGroupCollection: Type[],
    ...organisationUnitGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const organisationUnitGroups: Type[] = organisationUnitGroupsToCheck.filter(isPresent);
    if (organisationUnitGroups.length > 0) {
      const organisationUnitGroupCollectionIdentifiers = organisationUnitGroupCollection.map(
        organisationUnitGroupItem => this.getOrganisationUnitGroupIdentifier(organisationUnitGroupItem)!
      );
      const organisationUnitGroupsToAdd = organisationUnitGroups.filter(organisationUnitGroupItem => {
        const organisationUnitGroupIdentifier = this.getOrganisationUnitGroupIdentifier(organisationUnitGroupItem);
        if (organisationUnitGroupCollectionIdentifiers.includes(organisationUnitGroupIdentifier)) {
          return false;
        }
        organisationUnitGroupCollectionIdentifiers.push(organisationUnitGroupIdentifier);
        return true;
      });
      return [...organisationUnitGroupsToAdd, ...organisationUnitGroupCollection];
    }
    return organisationUnitGroupCollection;
  }

  protected convertDateFromClient<T extends IOrganisationUnitGroup | NewOrganisationUnitGroup | PartialUpdateOrganisationUnitGroup>(
    organisationUnitGroup: T
  ): RestOf<T> {
    return {
      ...organisationUnitGroup,
      created: organisationUnitGroup.created?.toJSON() ?? null,
      updated: organisationUnitGroup.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOrganisationUnitGroup: RestOrganisationUnitGroup): IOrganisationUnitGroup {
    return {
      ...restOrganisationUnitGroup,
      created: restOrganisationUnitGroup.created ? dayjs(restOrganisationUnitGroup.created) : undefined,
      updated: restOrganisationUnitGroup.updated ? dayjs(restOrganisationUnitGroup.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrganisationUnitGroup>): HttpResponse<IOrganisationUnitGroup> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrganisationUnitGroup[]>): HttpResponse<IOrganisationUnitGroup[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
