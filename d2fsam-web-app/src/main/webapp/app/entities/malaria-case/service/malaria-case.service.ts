import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMalariaCase, NewMalariaCase } from '../malaria-case.model';

export type PartialUpdateMalariaCase = Partial<IMalariaCase> & Pick<IMalariaCase, 'id'>;

type RestOf<T extends IMalariaCase | NewMalariaCase> = Omit<
  T,
  'entryStarted' | 'lastSynced' | 'dateOfExamination' | 'created' | 'updated' | 'createdAtClient' | 'updatedAtClient' | 'deletedAt'
> & {
  entryStarted?: string | null;
  lastSynced?: string | null;
  dateOfExamination?: string | null;
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  deletedAt?: string | null;
};

export type RestMalariaCase = RestOf<IMalariaCase>;

export type NewRestMalariaCase = RestOf<NewMalariaCase>;

export type PartialUpdateRestMalariaCase = RestOf<PartialUpdateMalariaCase>;

export type EntityResponseType = HttpResponse<IMalariaCase>;
export type EntityArrayResponseType = HttpResponse<IMalariaCase[]>;

@Injectable({ providedIn: 'root' })
export class MalariaCaseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/malaria-cases');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(malariaCase: NewMalariaCase): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(malariaCase);
    return this.http
      .post<RestMalariaCase>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(malariaCase: IMalariaCase): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(malariaCase);
    return this.http
      .put<RestMalariaCase>(`${this.resourceUrl}/${this.getMalariaCaseIdentifier(malariaCase)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(malariaCase: PartialUpdateMalariaCase): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(malariaCase);
    return this.http
      .patch<RestMalariaCase>(`${this.resourceUrl}/${this.getMalariaCaseIdentifier(malariaCase)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMalariaCase>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMalariaCase[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMalariaCaseIdentifier(malariaCase: Pick<IMalariaCase, 'id'>): number {
    return malariaCase.id;
  }

  compareMalariaCase(o1: Pick<IMalariaCase, 'id'> | null, o2: Pick<IMalariaCase, 'id'> | null): boolean {
    return o1 && o2 ? this.getMalariaCaseIdentifier(o1) === this.getMalariaCaseIdentifier(o2) : o1 === o2;
  }

  addMalariaCaseToCollectionIfMissing<Type extends Pick<IMalariaCase, 'id'>>(
    malariaCaseCollection: Type[],
    ...malariaCasesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const malariaCases: Type[] = malariaCasesToCheck.filter(isPresent);
    if (malariaCases.length > 0) {
      const malariaCaseCollectionIdentifiers = malariaCaseCollection.map(
        malariaCaseItem => this.getMalariaCaseIdentifier(malariaCaseItem)!
      );
      const malariaCasesToAdd = malariaCases.filter(malariaCaseItem => {
        const malariaCaseIdentifier = this.getMalariaCaseIdentifier(malariaCaseItem);
        if (malariaCaseCollectionIdentifiers.includes(malariaCaseIdentifier)) {
          return false;
        }
        malariaCaseCollectionIdentifiers.push(malariaCaseIdentifier);
        return true;
      });
      return [...malariaCasesToAdd, ...malariaCaseCollection];
    }
    return malariaCaseCollection;
  }

  protected convertDateFromClient<T extends IMalariaCase | NewMalariaCase | PartialUpdateMalariaCase>(malariaCase: T): RestOf<T> {
    return {
      ...malariaCase,
      entryStarted: malariaCase.entryStarted?.toJSON() ?? null,
      lastSynced: malariaCase.lastSynced?.toJSON() ?? null,
      dateOfExamination: malariaCase.dateOfExamination?.format(DATE_FORMAT) ?? null,
      created: malariaCase.created?.toJSON() ?? null,
      updated: malariaCase.updated?.toJSON() ?? null,
      createdAtClient: malariaCase.createdAtClient?.toJSON() ?? null,
      updatedAtClient: malariaCase.updatedAtClient?.toJSON() ?? null,
      deletedAt: malariaCase.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMalariaCase: RestMalariaCase): IMalariaCase {
    return {
      ...restMalariaCase,
      entryStarted: restMalariaCase.entryStarted ? dayjs(restMalariaCase.entryStarted) : undefined,
      lastSynced: restMalariaCase.lastSynced ? dayjs(restMalariaCase.lastSynced) : undefined,
      dateOfExamination: restMalariaCase.dateOfExamination ? dayjs(restMalariaCase.dateOfExamination) : undefined,
      created: restMalariaCase.created ? dayjs(restMalariaCase.created) : undefined,
      updated: restMalariaCase.updated ? dayjs(restMalariaCase.updated) : undefined,
      createdAtClient: restMalariaCase.createdAtClient ? dayjs(restMalariaCase.createdAtClient) : undefined,
      updatedAtClient: restMalariaCase.updatedAtClient ? dayjs(restMalariaCase.updatedAtClient) : undefined,
      deletedAt: restMalariaCase.deletedAt ? dayjs(restMalariaCase.deletedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMalariaCase>): HttpResponse<IMalariaCase> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMalariaCase[]>): HttpResponse<IMalariaCase[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
