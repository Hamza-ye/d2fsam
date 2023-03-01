import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserGroup, NewUserGroup } from '../user-group.model';

export type PartialUpdateUserGroup = Partial<IUserGroup> & Pick<IUserGroup, 'id'>;

type RestOf<T extends IUserGroup | NewUserGroup> = Omit<T, 'created' | 'updated' | 'activeFrom' | 'activeTo'> & {
  created?: string | null;
  updated?: string | null;
  activeFrom?: string | null;
  activeTo?: string | null;
};

export type RestUserGroup = RestOf<IUserGroup>;

export type NewRestUserGroup = RestOf<NewUserGroup>;

export type PartialUpdateRestUserGroup = RestOf<PartialUpdateUserGroup>;

export type EntityResponseType = HttpResponse<IUserGroup>;
export type EntityArrayResponseType = HttpResponse<IUserGroup[]>;

@Injectable({ providedIn: 'root' })
export class UserGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userGroup: NewUserGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userGroup);
    return this.http
      .post<RestUserGroup>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userGroup: IUserGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userGroup);
    return this.http
      .put<RestUserGroup>(`${this.resourceUrl}/${this.getUserGroupIdentifier(userGroup)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userGroup: PartialUpdateUserGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userGroup);
    return this.http
      .patch<RestUserGroup>(`${this.resourceUrl}/${this.getUserGroupIdentifier(userGroup)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserGroupIdentifier(userGroup: Pick<IUserGroup, 'id'>): number {
    return userGroup.id;
  }

  compareUserGroup(o1: Pick<IUserGroup, 'id'> | null, o2: Pick<IUserGroup, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserGroupIdentifier(o1) === this.getUserGroupIdentifier(o2) : o1 === o2;
  }

  addUserGroupToCollectionIfMissing<Type extends Pick<IUserGroup, 'id'>>(
    userGroupCollection: Type[],
    ...userGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userGroups: Type[] = userGroupsToCheck.filter(isPresent);
    if (userGroups.length > 0) {
      const userGroupCollectionIdentifiers = userGroupCollection.map(userGroupItem => this.getUserGroupIdentifier(userGroupItem)!);
      const userGroupsToAdd = userGroups.filter(userGroupItem => {
        const userGroupIdentifier = this.getUserGroupIdentifier(userGroupItem);
        if (userGroupCollectionIdentifiers.includes(userGroupIdentifier)) {
          return false;
        }
        userGroupCollectionIdentifiers.push(userGroupIdentifier);
        return true;
      });
      return [...userGroupsToAdd, ...userGroupCollection];
    }
    return userGroupCollection;
  }

  protected convertDateFromClient<T extends IUserGroup | NewUserGroup | PartialUpdateUserGroup>(userGroup: T): RestOf<T> {
    return {
      ...userGroup,
      created: userGroup.created?.toJSON() ?? null,
      updated: userGroup.updated?.toJSON() ?? null,
      activeFrom: userGroup.activeFrom?.toJSON() ?? null,
      activeTo: userGroup.activeTo?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restUserGroup: RestUserGroup): IUserGroup {
    return {
      ...restUserGroup,
      created: restUserGroup.created ? dayjs(restUserGroup.created) : undefined,
      updated: restUserGroup.updated ? dayjs(restUserGroup.updated) : undefined,
      activeFrom: restUserGroup.activeFrom ? dayjs(restUserGroup.activeFrom) : undefined,
      activeTo: restUserGroup.activeTo ? dayjs(restUserGroup.activeTo) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserGroup>): HttpResponse<IUserGroup> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserGroup[]>): HttpResponse<IUserGroup[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
