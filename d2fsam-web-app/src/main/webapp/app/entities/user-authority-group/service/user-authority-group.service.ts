import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserAuthorityGroup, NewUserAuthorityGroup } from '../user-authority-group.model';

export type PartialUpdateUserAuthorityGroup = Partial<IUserAuthorityGroup> & Pick<IUserAuthorityGroup, 'id'>;

type RestOf<T extends IUserAuthorityGroup | NewUserAuthorityGroup> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestUserAuthorityGroup = RestOf<IUserAuthorityGroup>;

export type NewRestUserAuthorityGroup = RestOf<NewUserAuthorityGroup>;

export type PartialUpdateRestUserAuthorityGroup = RestOf<PartialUpdateUserAuthorityGroup>;

export type EntityResponseType = HttpResponse<IUserAuthorityGroup>;
export type EntityArrayResponseType = HttpResponse<IUserAuthorityGroup[]>;

@Injectable({ providedIn: 'root' })
export class UserAuthorityGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-authority-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userAuthorityGroup: NewUserAuthorityGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userAuthorityGroup);
    return this.http
      .post<RestUserAuthorityGroup>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userAuthorityGroup: IUserAuthorityGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userAuthorityGroup);
    return this.http
      .put<RestUserAuthorityGroup>(`${this.resourceUrl}/${this.getUserAuthorityGroupIdentifier(userAuthorityGroup)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userAuthorityGroup: PartialUpdateUserAuthorityGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userAuthorityGroup);
    return this.http
      .patch<RestUserAuthorityGroup>(`${this.resourceUrl}/${this.getUserAuthorityGroupIdentifier(userAuthorityGroup)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserAuthorityGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserAuthorityGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserAuthorityGroupIdentifier(userAuthorityGroup: Pick<IUserAuthorityGroup, 'id'>): number {
    return userAuthorityGroup.id;
  }

  compareUserAuthorityGroup(o1: Pick<IUserAuthorityGroup, 'id'> | null, o2: Pick<IUserAuthorityGroup, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserAuthorityGroupIdentifier(o1) === this.getUserAuthorityGroupIdentifier(o2) : o1 === o2;
  }

  addUserAuthorityGroupToCollectionIfMissing<Type extends Pick<IUserAuthorityGroup, 'id'>>(
    userAuthorityGroupCollection: Type[],
    ...userAuthorityGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userAuthorityGroups: Type[] = userAuthorityGroupsToCheck.filter(isPresent);
    if (userAuthorityGroups.length > 0) {
      const userAuthorityGroupCollectionIdentifiers = userAuthorityGroupCollection.map(
        userAuthorityGroupItem => this.getUserAuthorityGroupIdentifier(userAuthorityGroupItem)!
      );
      const userAuthorityGroupsToAdd = userAuthorityGroups.filter(userAuthorityGroupItem => {
        const userAuthorityGroupIdentifier = this.getUserAuthorityGroupIdentifier(userAuthorityGroupItem);
        if (userAuthorityGroupCollectionIdentifiers.includes(userAuthorityGroupIdentifier)) {
          return false;
        }
        userAuthorityGroupCollectionIdentifiers.push(userAuthorityGroupIdentifier);
        return true;
      });
      return [...userAuthorityGroupsToAdd, ...userAuthorityGroupCollection];
    }
    return userAuthorityGroupCollection;
  }

  protected convertDateFromClient<T extends IUserAuthorityGroup | NewUserAuthorityGroup | PartialUpdateUserAuthorityGroup>(
    userAuthorityGroup: T
  ): RestOf<T> {
    return {
      ...userAuthorityGroup,
      created: userAuthorityGroup.created?.toJSON() ?? null,
      updated: userAuthorityGroup.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restUserAuthorityGroup: RestUserAuthorityGroup): IUserAuthorityGroup {
    return {
      ...restUserAuthorityGroup,
      created: restUserAuthorityGroup.created ? dayjs(restUserAuthorityGroup.created) : undefined,
      updated: restUserAuthorityGroup.updated ? dayjs(restUserAuthorityGroup.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserAuthorityGroup>): HttpResponse<IUserAuthorityGroup> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserAuthorityGroup[]>): HttpResponse<IUserAuthorityGroup[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
