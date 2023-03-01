import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOptionSet, NewOptionSet } from '../option-set.model';

export type PartialUpdateOptionSet = Partial<IOptionSet> & Pick<IOptionSet, 'id'>;

type RestOf<T extends IOptionSet | NewOptionSet> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestOptionSet = RestOf<IOptionSet>;

export type NewRestOptionSet = RestOf<NewOptionSet>;

export type PartialUpdateRestOptionSet = RestOf<PartialUpdateOptionSet>;

export type EntityResponseType = HttpResponse<IOptionSet>;
export type EntityArrayResponseType = HttpResponse<IOptionSet[]>;

@Injectable({ providedIn: 'root' })
export class OptionSetService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/option-sets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(optionSet: NewOptionSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(optionSet);
    return this.http
      .post<RestOptionSet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(optionSet: IOptionSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(optionSet);
    return this.http
      .put<RestOptionSet>(`${this.resourceUrl}/${this.getOptionSetIdentifier(optionSet)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(optionSet: PartialUpdateOptionSet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(optionSet);
    return this.http
      .patch<RestOptionSet>(`${this.resourceUrl}/${this.getOptionSetIdentifier(optionSet)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOptionSet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOptionSet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOptionSetIdentifier(optionSet: Pick<IOptionSet, 'id'>): number {
    return optionSet.id;
  }

  compareOptionSet(o1: Pick<IOptionSet, 'id'> | null, o2: Pick<IOptionSet, 'id'> | null): boolean {
    return o1 && o2 ? this.getOptionSetIdentifier(o1) === this.getOptionSetIdentifier(o2) : o1 === o2;
  }

  addOptionSetToCollectionIfMissing<Type extends Pick<IOptionSet, 'id'>>(
    optionSetCollection: Type[],
    ...optionSetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const optionSets: Type[] = optionSetsToCheck.filter(isPresent);
    if (optionSets.length > 0) {
      const optionSetCollectionIdentifiers = optionSetCollection.map(optionSetItem => this.getOptionSetIdentifier(optionSetItem)!);
      const optionSetsToAdd = optionSets.filter(optionSetItem => {
        const optionSetIdentifier = this.getOptionSetIdentifier(optionSetItem);
        if (optionSetCollectionIdentifiers.includes(optionSetIdentifier)) {
          return false;
        }
        optionSetCollectionIdentifiers.push(optionSetIdentifier);
        return true;
      });
      return [...optionSetsToAdd, ...optionSetCollection];
    }
    return optionSetCollection;
  }

  protected convertDateFromClient<T extends IOptionSet | NewOptionSet | PartialUpdateOptionSet>(optionSet: T): RestOf<T> {
    return {
      ...optionSet,
      created: optionSet.created?.toJSON() ?? null,
      updated: optionSet.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOptionSet: RestOptionSet): IOptionSet {
    return {
      ...restOptionSet,
      created: restOptionSet.created ? dayjs(restOptionSet.created) : undefined,
      updated: restOptionSet.updated ? dayjs(restOptionSet.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOptionSet>): HttpResponse<IOptionSet> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOptionSet[]>): HttpResponse<IOptionSet[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
