import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserData, NewUserData } from '../user-data.model';

export type PartialUpdateUserData = Partial<IUserData> & Pick<IUserData, 'id'>;

type RestOf<T extends IUserData | NewUserData> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestUserData = RestOf<IUserData>;

export type NewRestUserData = RestOf<NewUserData>;

export type PartialUpdateRestUserData = RestOf<PartialUpdateUserData>;

export type EntityResponseType = HttpResponse<IUserData>;
export type EntityArrayResponseType = HttpResponse<IUserData[]>;

@Injectable({ providedIn: 'root' })
export class UserDataService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-data');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userData: NewUserData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userData);
    return this.http
      .post<RestUserData>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userData: IUserData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userData);
    return this.http
      .put<RestUserData>(`${this.resourceUrl}/${this.getUserDataIdentifier(userData)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userData: PartialUpdateUserData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userData);
    return this.http
      .patch<RestUserData>(`${this.resourceUrl}/${this.getUserDataIdentifier(userData)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserData>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserData[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserDataIdentifier(userData: Pick<IUserData, 'id'>): number {
    return userData.id;
  }

  compareUserData(o1: Pick<IUserData, 'id'> | null, o2: Pick<IUserData, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserDataIdentifier(o1) === this.getUserDataIdentifier(o2) : o1 === o2;
  }

  addUserDataToCollectionIfMissing<Type extends Pick<IUserData, 'id'>>(
    userDataCollection: Type[],
    ...userDataToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userData: Type[] = userDataToCheck.filter(isPresent);
    if (userData.length > 0) {
      const userDataCollectionIdentifiers = userDataCollection.map(userDataItem => this.getUserDataIdentifier(userDataItem)!);
      const userDataToAdd = userData.filter(userDataItem => {
        const userDataIdentifier = this.getUserDataIdentifier(userDataItem);
        if (userDataCollectionIdentifiers.includes(userDataIdentifier)) {
          return false;
        }
        userDataCollectionIdentifiers.push(userDataIdentifier);
        return true;
      });
      return [...userDataToAdd, ...userDataCollection];
    }
    return userDataCollection;
  }

  protected convertDateFromClient<T extends IUserData | NewUserData | PartialUpdateUserData>(userData: T): RestOf<T> {
    return {
      ...userData,
      created: userData.created?.toJSON() ?? null,
      updated: userData.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restUserData: RestUserData): IUserData {
    return {
      ...restUserData,
      created: restUserData.created ? dayjs(restUserData.created) : undefined,
      updated: restUserData.updated ? dayjs(restUserData.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserData>): HttpResponse<IUserData> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserData[]>): HttpResponse<IUserData[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
