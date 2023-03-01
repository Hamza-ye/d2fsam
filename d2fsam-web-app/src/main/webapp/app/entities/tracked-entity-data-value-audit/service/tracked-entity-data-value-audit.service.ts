import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityDataValueAudit, NewTrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';

export type PartialUpdateTrackedEntityDataValueAudit = Partial<ITrackedEntityDataValueAudit> & Pick<ITrackedEntityDataValueAudit, 'id'>;

type RestOf<T extends ITrackedEntityDataValueAudit | NewTrackedEntityDataValueAudit> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityDataValueAudit = RestOf<ITrackedEntityDataValueAudit>;

export type NewRestTrackedEntityDataValueAudit = RestOf<NewTrackedEntityDataValueAudit>;

export type PartialUpdateRestTrackedEntityDataValueAudit = RestOf<PartialUpdateTrackedEntityDataValueAudit>;

export type EntityResponseType = HttpResponse<ITrackedEntityDataValueAudit>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityDataValueAudit[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityDataValueAuditService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-data-value-audits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityDataValueAudit: NewTrackedEntityDataValueAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityDataValueAudit);
    return this.http
      .post<RestTrackedEntityDataValueAudit>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityDataValueAudit: ITrackedEntityDataValueAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityDataValueAudit);
    return this.http
      .put<RestTrackedEntityDataValueAudit>(
        `${this.resourceUrl}/${this.getTrackedEntityDataValueAuditIdentifier(trackedEntityDataValueAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityDataValueAudit: PartialUpdateTrackedEntityDataValueAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityDataValueAudit);
    return this.http
      .patch<RestTrackedEntityDataValueAudit>(
        `${this.resourceUrl}/${this.getTrackedEntityDataValueAuditIdentifier(trackedEntityDataValueAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityDataValueAudit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityDataValueAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityDataValueAuditIdentifier(trackedEntityDataValueAudit: Pick<ITrackedEntityDataValueAudit, 'id'>): number {
    return trackedEntityDataValueAudit.id;
  }

  compareTrackedEntityDataValueAudit(
    o1: Pick<ITrackedEntityDataValueAudit, 'id'> | null,
    o2: Pick<ITrackedEntityDataValueAudit, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTrackedEntityDataValueAuditIdentifier(o1) === this.getTrackedEntityDataValueAuditIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityDataValueAuditToCollectionIfMissing<Type extends Pick<ITrackedEntityDataValueAudit, 'id'>>(
    trackedEntityDataValueAuditCollection: Type[],
    ...trackedEntityDataValueAuditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityDataValueAudits: Type[] = trackedEntityDataValueAuditsToCheck.filter(isPresent);
    if (trackedEntityDataValueAudits.length > 0) {
      const trackedEntityDataValueAuditCollectionIdentifiers = trackedEntityDataValueAuditCollection.map(
        trackedEntityDataValueAuditItem => this.getTrackedEntityDataValueAuditIdentifier(trackedEntityDataValueAuditItem)!
      );
      const trackedEntityDataValueAuditsToAdd = trackedEntityDataValueAudits.filter(trackedEntityDataValueAuditItem => {
        const trackedEntityDataValueAuditIdentifier = this.getTrackedEntityDataValueAuditIdentifier(trackedEntityDataValueAuditItem);
        if (trackedEntityDataValueAuditCollectionIdentifiers.includes(trackedEntityDataValueAuditIdentifier)) {
          return false;
        }
        trackedEntityDataValueAuditCollectionIdentifiers.push(trackedEntityDataValueAuditIdentifier);
        return true;
      });
      return [...trackedEntityDataValueAuditsToAdd, ...trackedEntityDataValueAuditCollection];
    }
    return trackedEntityDataValueAuditCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityDataValueAudit | NewTrackedEntityDataValueAudit | PartialUpdateTrackedEntityDataValueAudit
  >(trackedEntityDataValueAudit: T): RestOf<T> {
    return {
      ...trackedEntityDataValueAudit,
      created: trackedEntityDataValueAudit.created?.toJSON() ?? null,
      updated: trackedEntityDataValueAudit.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityDataValueAudit: RestTrackedEntityDataValueAudit): ITrackedEntityDataValueAudit {
    return {
      ...restTrackedEntityDataValueAudit,
      created: restTrackedEntityDataValueAudit.created ? dayjs(restTrackedEntityDataValueAudit.created) : undefined,
      updated: restTrackedEntityDataValueAudit.updated ? dayjs(restTrackedEntityDataValueAudit.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityDataValueAudit>): HttpResponse<ITrackedEntityDataValueAudit> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestTrackedEntityDataValueAudit[]>
  ): HttpResponse<ITrackedEntityDataValueAudit[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
