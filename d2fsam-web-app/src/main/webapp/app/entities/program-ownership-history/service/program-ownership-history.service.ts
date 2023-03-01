import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramOwnershipHistory, NewProgramOwnershipHistory } from '../program-ownership-history.model';

export type PartialUpdateProgramOwnershipHistory = Partial<IProgramOwnershipHistory> & Pick<IProgramOwnershipHistory, 'id'>;

type RestOf<T extends IProgramOwnershipHistory | NewProgramOwnershipHistory> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestProgramOwnershipHistory = RestOf<IProgramOwnershipHistory>;

export type NewRestProgramOwnershipHistory = RestOf<NewProgramOwnershipHistory>;

export type PartialUpdateRestProgramOwnershipHistory = RestOf<PartialUpdateProgramOwnershipHistory>;

export type EntityResponseType = HttpResponse<IProgramOwnershipHistory>;
export type EntityArrayResponseType = HttpResponse<IProgramOwnershipHistory[]>;

@Injectable({ providedIn: 'root' })
export class ProgramOwnershipHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-ownership-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programOwnershipHistory: NewProgramOwnershipHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programOwnershipHistory);
    return this.http
      .post<RestProgramOwnershipHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programOwnershipHistory: IProgramOwnershipHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programOwnershipHistory);
    return this.http
      .put<RestProgramOwnershipHistory>(`${this.resourceUrl}/${this.getProgramOwnershipHistoryIdentifier(programOwnershipHistory)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programOwnershipHistory: PartialUpdateProgramOwnershipHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programOwnershipHistory);
    return this.http
      .patch<RestProgramOwnershipHistory>(
        `${this.resourceUrl}/${this.getProgramOwnershipHistoryIdentifier(programOwnershipHistory)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramOwnershipHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramOwnershipHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramOwnershipHistoryIdentifier(programOwnershipHistory: Pick<IProgramOwnershipHistory, 'id'>): number {
    return programOwnershipHistory.id;
  }

  compareProgramOwnershipHistory(
    o1: Pick<IProgramOwnershipHistory, 'id'> | null,
    o2: Pick<IProgramOwnershipHistory, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getProgramOwnershipHistoryIdentifier(o1) === this.getProgramOwnershipHistoryIdentifier(o2) : o1 === o2;
  }

  addProgramOwnershipHistoryToCollectionIfMissing<Type extends Pick<IProgramOwnershipHistory, 'id'>>(
    programOwnershipHistoryCollection: Type[],
    ...programOwnershipHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programOwnershipHistories: Type[] = programOwnershipHistoriesToCheck.filter(isPresent);
    if (programOwnershipHistories.length > 0) {
      const programOwnershipHistoryCollectionIdentifiers = programOwnershipHistoryCollection.map(
        programOwnershipHistoryItem => this.getProgramOwnershipHistoryIdentifier(programOwnershipHistoryItem)!
      );
      const programOwnershipHistoriesToAdd = programOwnershipHistories.filter(programOwnershipHistoryItem => {
        const programOwnershipHistoryIdentifier = this.getProgramOwnershipHistoryIdentifier(programOwnershipHistoryItem);
        if (programOwnershipHistoryCollectionIdentifiers.includes(programOwnershipHistoryIdentifier)) {
          return false;
        }
        programOwnershipHistoryCollectionIdentifiers.push(programOwnershipHistoryIdentifier);
        return true;
      });
      return [...programOwnershipHistoriesToAdd, ...programOwnershipHistoryCollection];
    }
    return programOwnershipHistoryCollection;
  }

  protected convertDateFromClient<T extends IProgramOwnershipHistory | NewProgramOwnershipHistory | PartialUpdateProgramOwnershipHistory>(
    programOwnershipHistory: T
  ): RestOf<T> {
    return {
      ...programOwnershipHistory,
      startDate: programOwnershipHistory.startDate?.toJSON() ?? null,
      endDate: programOwnershipHistory.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramOwnershipHistory: RestProgramOwnershipHistory): IProgramOwnershipHistory {
    return {
      ...restProgramOwnershipHistory,
      startDate: restProgramOwnershipHistory.startDate ? dayjs(restProgramOwnershipHistory.startDate) : undefined,
      endDate: restProgramOwnershipHistory.endDate ? dayjs(restProgramOwnershipHistory.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramOwnershipHistory>): HttpResponse<IProgramOwnershipHistory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramOwnershipHistory[]>): HttpResponse<IProgramOwnershipHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
