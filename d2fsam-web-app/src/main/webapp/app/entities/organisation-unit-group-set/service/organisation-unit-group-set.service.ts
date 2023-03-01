import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrganisationUnitGroupSet, NewOrganisationUnitGroupSet } from '../organisation-unit-group-set.model';

export type PartialUpdateOrganisationUnitGroupSet = Partial<IOrganisationUnitGroupSet> & Pick<IOrganisationUnitGroupSet, 'id'>;

type RestOf<T extends IOrganisationUnitGroupSet | NewOrganisationUnitGroupSet> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestOrganisationUnitGroupSet = RestOf<IOrganisationUnitGroupSet>;

export type NewRestOrganisationUnitGroupSet = RestOf<NewOrganisationUnitGroupSet>;

export type PartialUpdateRestOrganisationUnitGroupSet = RestOf<PartialUpdateOrganisationUnitGroupSet>;

export type EntityResponseType = HttpResponse<IOrganisationUnitGroupSet>;
export type EntityArrayResponseType = HttpResponse<IOrganisationUnitGroupSet[]>;

@Injectable({ providedIn: 'root' })
export class OrganisationUnitGroupSetService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/organisation-unit-group-sets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(organisationUnitGroupSet: NewOrganisationUnitGroupSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitGroupSet);
    return this.http
      .post<RestOrganisationUnitGroupSet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(organisationUnitGroupSet: IOrganisationUnitGroupSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitGroupSet);
    return this.http
      .put<RestOrganisationUnitGroupSet>(
        `${this.resourceUrl}/${this.getOrganisationUnitGroupSetIdentifier(organisationUnitGroupSet)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(organisationUnitGroupSet: PartialUpdateOrganisationUnitGroupSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(organisationUnitGroupSet);
    return this.http
      .patch<RestOrganisationUnitGroupSet>(
        `${this.resourceUrl}/${this.getOrganisationUnitGroupSetIdentifier(organisationUnitGroupSet)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrganisationUnitGroupSet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrganisationUnitGroupSet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOrganisationUnitGroupSetIdentifier(organisationUnitGroupSet: Pick<IOrganisationUnitGroupSet, 'id'>): number {
    return organisationUnitGroupSet.id;
  }

  compareOrganisationUnitGroupSet(
    o1: Pick<IOrganisationUnitGroupSet, 'id'> | null,
    o2: Pick<IOrganisationUnitGroupSet, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getOrganisationUnitGroupSetIdentifier(o1) === this.getOrganisationUnitGroupSetIdentifier(o2) : o1 === o2;
  }

  addOrganisationUnitGroupSetToCollectionIfMissing<Type extends Pick<IOrganisationUnitGroupSet, 'id'>>(
    organisationUnitGroupSetCollection: Type[],
    ...organisationUnitGroupSetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const organisationUnitGroupSets: Type[] = organisationUnitGroupSetsToCheck.filter(isPresent);
    if (organisationUnitGroupSets.length > 0) {
      const organisationUnitGroupSetCollectionIdentifiers = organisationUnitGroupSetCollection.map(
        organisationUnitGroupSetItem => this.getOrganisationUnitGroupSetIdentifier(organisationUnitGroupSetItem)!
      );
      const organisationUnitGroupSetsToAdd = organisationUnitGroupSets.filter(organisationUnitGroupSetItem => {
        const organisationUnitGroupSetIdentifier = this.getOrganisationUnitGroupSetIdentifier(organisationUnitGroupSetItem);
        if (organisationUnitGroupSetCollectionIdentifiers.includes(organisationUnitGroupSetIdentifier)) {
          return false;
        }
        organisationUnitGroupSetCollectionIdentifiers.push(organisationUnitGroupSetIdentifier);
        return true;
      });
      return [...organisationUnitGroupSetsToAdd, ...organisationUnitGroupSetCollection];
    }
    return organisationUnitGroupSetCollection;
  }

  protected convertDateFromClient<
    T extends IOrganisationUnitGroupSet | NewOrganisationUnitGroupSet | PartialUpdateOrganisationUnitGroupSet
  >(organisationUnitGroupSet: T): RestOf<T> {
    return {
      ...organisationUnitGroupSet,
      created: organisationUnitGroupSet.created?.toJSON() ?? null,
      updated: organisationUnitGroupSet.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOrganisationUnitGroupSet: RestOrganisationUnitGroupSet): IOrganisationUnitGroupSet {
    return {
      ...restOrganisationUnitGroupSet,
      created: restOrganisationUnitGroupSet.created ? dayjs(restOrganisationUnitGroupSet.created) : undefined,
      updated: restOrganisationUnitGroupSet.updated ? dayjs(restOrganisationUnitGroupSet.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrganisationUnitGroupSet>): HttpResponse<IOrganisationUnitGroupSet> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrganisationUnitGroupSet[]>): HttpResponse<IOrganisationUnitGroupSet[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
