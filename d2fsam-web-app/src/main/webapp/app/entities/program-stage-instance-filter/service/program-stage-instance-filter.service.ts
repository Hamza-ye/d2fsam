import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramStageInstanceFilter, NewProgramStageInstanceFilter } from '../program-stage-instance-filter.model';

export type PartialUpdateProgramStageInstanceFilter = Partial<IProgramStageInstanceFilter> & Pick<IProgramStageInstanceFilter, 'id'>;

type RestOf<T extends IProgramStageInstanceFilter | NewProgramStageInstanceFilter> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestProgramStageInstanceFilter = RestOf<IProgramStageInstanceFilter>;

export type NewRestProgramStageInstanceFilter = RestOf<NewProgramStageInstanceFilter>;

export type PartialUpdateRestProgramStageInstanceFilter = RestOf<PartialUpdateProgramStageInstanceFilter>;

export type EntityResponseType = HttpResponse<IProgramStageInstanceFilter>;
export type EntityArrayResponseType = HttpResponse<IProgramStageInstanceFilter[]>;

@Injectable({ providedIn: 'root' })
export class ProgramStageInstanceFilterService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-stage-instance-filters');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programStageInstanceFilter: NewProgramStageInstanceFilter): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageInstanceFilter);
    return this.http
      .post<RestProgramStageInstanceFilter>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programStageInstanceFilter: IProgramStageInstanceFilter): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageInstanceFilter);
    return this.http
      .put<RestProgramStageInstanceFilter>(
        `${this.resourceUrl}/${this.getProgramStageInstanceFilterIdentifier(programStageInstanceFilter)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programStageInstanceFilter: PartialUpdateProgramStageInstanceFilter): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageInstanceFilter);
    return this.http
      .patch<RestProgramStageInstanceFilter>(
        `${this.resourceUrl}/${this.getProgramStageInstanceFilterIdentifier(programStageInstanceFilter)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramStageInstanceFilter>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramStageInstanceFilter[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramStageInstanceFilterIdentifier(programStageInstanceFilter: Pick<IProgramStageInstanceFilter, 'id'>): number {
    return programStageInstanceFilter.id;
  }

  compareProgramStageInstanceFilter(
    o1: Pick<IProgramStageInstanceFilter, 'id'> | null,
    o2: Pick<IProgramStageInstanceFilter, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getProgramStageInstanceFilterIdentifier(o1) === this.getProgramStageInstanceFilterIdentifier(o2) : o1 === o2;
  }

  addProgramStageInstanceFilterToCollectionIfMissing<Type extends Pick<IProgramStageInstanceFilter, 'id'>>(
    programStageInstanceFilterCollection: Type[],
    ...programStageInstanceFiltersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programStageInstanceFilters: Type[] = programStageInstanceFiltersToCheck.filter(isPresent);
    if (programStageInstanceFilters.length > 0) {
      const programStageInstanceFilterCollectionIdentifiers = programStageInstanceFilterCollection.map(
        programStageInstanceFilterItem => this.getProgramStageInstanceFilterIdentifier(programStageInstanceFilterItem)!
      );
      const programStageInstanceFiltersToAdd = programStageInstanceFilters.filter(programStageInstanceFilterItem => {
        const programStageInstanceFilterIdentifier = this.getProgramStageInstanceFilterIdentifier(programStageInstanceFilterItem);
        if (programStageInstanceFilterCollectionIdentifiers.includes(programStageInstanceFilterIdentifier)) {
          return false;
        }
        programStageInstanceFilterCollectionIdentifiers.push(programStageInstanceFilterIdentifier);
        return true;
      });
      return [...programStageInstanceFiltersToAdd, ...programStageInstanceFilterCollection];
    }
    return programStageInstanceFilterCollection;
  }

  protected convertDateFromClient<
    T extends IProgramStageInstanceFilter | NewProgramStageInstanceFilter | PartialUpdateProgramStageInstanceFilter
  >(programStageInstanceFilter: T): RestOf<T> {
    return {
      ...programStageInstanceFilter,
      created: programStageInstanceFilter.created?.toJSON() ?? null,
      updated: programStageInstanceFilter.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramStageInstanceFilter: RestProgramStageInstanceFilter): IProgramStageInstanceFilter {
    return {
      ...restProgramStageInstanceFilter,
      created: restProgramStageInstanceFilter.created ? dayjs(restProgramStageInstanceFilter.created) : undefined,
      updated: restProgramStageInstanceFilter.updated ? dayjs(restProgramStageInstanceFilter.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramStageInstanceFilter>): HttpResponse<IProgramStageInstanceFilter> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestProgramStageInstanceFilter[]>
  ): HttpResponse<IProgramStageInstanceFilter[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
