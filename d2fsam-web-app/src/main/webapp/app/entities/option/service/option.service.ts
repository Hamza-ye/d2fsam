import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOption, NewOption } from '../option.model';

export type PartialUpdateOption = Partial<IOption> & Pick<IOption, 'id'>;

type RestOf<T extends IOption | NewOption> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestOption = RestOf<IOption>;

export type NewRestOption = RestOf<NewOption>;

export type PartialUpdateRestOption = RestOf<PartialUpdateOption>;

export type EntityResponseType = HttpResponse<IOption>;
export type EntityArrayResponseType = HttpResponse<IOption[]>;

@Injectable({ providedIn: 'root' })
export class OptionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/options');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(option: NewOption): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(option);
    return this.http
      .post<RestOption>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(option: IOption): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(option);
    return this.http
      .put<RestOption>(`${this.resourceUrl}/${this.getOptionIdentifier(option)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(option: PartialUpdateOption): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(option);
    return this.http
      .patch<RestOption>(`${this.resourceUrl}/${this.getOptionIdentifier(option)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOption>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOption[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOptionIdentifier(option: Pick<IOption, 'id'>): number {
    return option.id;
  }

  compareOption(o1: Pick<IOption, 'id'> | null, o2: Pick<IOption, 'id'> | null): boolean {
    return o1 && o2 ? this.getOptionIdentifier(o1) === this.getOptionIdentifier(o2) : o1 === o2;
  }

  addOptionToCollectionIfMissing<Type extends Pick<IOption, 'id'>>(
    optionCollection: Type[],
    ...optionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const options: Type[] = optionsToCheck.filter(isPresent);
    if (options.length > 0) {
      const optionCollectionIdentifiers = optionCollection.map(optionItem => this.getOptionIdentifier(optionItem)!);
      const optionsToAdd = options.filter(optionItem => {
        const optionIdentifier = this.getOptionIdentifier(optionItem);
        if (optionCollectionIdentifiers.includes(optionIdentifier)) {
          return false;
        }
        optionCollectionIdentifiers.push(optionIdentifier);
        return true;
      });
      return [...optionsToAdd, ...optionCollection];
    }
    return optionCollection;
  }

  protected convertDateFromClient<T extends IOption | NewOption | PartialUpdateOption>(option: T): RestOf<T> {
    return {
      ...option,
      created: option.created?.toJSON() ?? null,
      updated: option.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOption: RestOption): IOption {
    return {
      ...restOption,
      created: restOption.created ? dayjs(restOption.created) : undefined,
      updated: restOption.updated ? dayjs(restOption.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOption>): HttpResponse<IOption> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOption[]>): HttpResponse<IOption[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
