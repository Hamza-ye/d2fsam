import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityAttributeValueAudit, NewTrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';

export type PartialUpdateTrackedEntityAttributeValueAudit = Partial<ITrackedEntityAttributeValueAudit> &
  Pick<ITrackedEntityAttributeValueAudit, 'id'>;

type RestOf<T extends ITrackedEntityAttributeValueAudit | NewTrackedEntityAttributeValueAudit> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestTrackedEntityAttributeValueAudit = RestOf<ITrackedEntityAttributeValueAudit>;

export type NewRestTrackedEntityAttributeValueAudit = RestOf<NewTrackedEntityAttributeValueAudit>;

export type PartialUpdateRestTrackedEntityAttributeValueAudit = RestOf<PartialUpdateTrackedEntityAttributeValueAudit>;

export type EntityResponseType = HttpResponse<ITrackedEntityAttributeValueAudit>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityAttributeValueAudit[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityAttributeValueAuditService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-attribute-value-audits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityAttributeValueAudit: NewTrackedEntityAttributeValueAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityAttributeValueAudit);
    return this.http
      .post<RestTrackedEntityAttributeValueAudit>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityAttributeValueAudit);
    return this.http
      .put<RestTrackedEntityAttributeValueAudit>(
        `${this.resourceUrl}/${this.getTrackedEntityAttributeValueAuditIdentifier(trackedEntityAttributeValueAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityAttributeValueAudit: PartialUpdateTrackedEntityAttributeValueAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityAttributeValueAudit);
    return this.http
      .patch<RestTrackedEntityAttributeValueAudit>(
        `${this.resourceUrl}/${this.getTrackedEntityAttributeValueAuditIdentifier(trackedEntityAttributeValueAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityAttributeValueAudit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityAttributeValueAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityAttributeValueAuditIdentifier(trackedEntityAttributeValueAudit: Pick<ITrackedEntityAttributeValueAudit, 'id'>): number {
    return trackedEntityAttributeValueAudit.id;
  }

  compareTrackedEntityAttributeValueAudit(
    o1: Pick<ITrackedEntityAttributeValueAudit, 'id'> | null,
    o2: Pick<ITrackedEntityAttributeValueAudit, 'id'> | null
  ): boolean {
    return o1 && o2
      ? this.getTrackedEntityAttributeValueAuditIdentifier(o1) === this.getTrackedEntityAttributeValueAuditIdentifier(o2)
      : o1 === o2;
  }

  addTrackedEntityAttributeValueAuditToCollectionIfMissing<Type extends Pick<ITrackedEntityAttributeValueAudit, 'id'>>(
    trackedEntityAttributeValueAuditCollection: Type[],
    ...trackedEntityAttributeValueAuditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityAttributeValueAudits: Type[] = trackedEntityAttributeValueAuditsToCheck.filter(isPresent);
    if (trackedEntityAttributeValueAudits.length > 0) {
      const trackedEntityAttributeValueAuditCollectionIdentifiers = trackedEntityAttributeValueAuditCollection.map(
        trackedEntityAttributeValueAuditItem => this.getTrackedEntityAttributeValueAuditIdentifier(trackedEntityAttributeValueAuditItem)!
      );
      const trackedEntityAttributeValueAuditsToAdd = trackedEntityAttributeValueAudits.filter(trackedEntityAttributeValueAuditItem => {
        const trackedEntityAttributeValueAuditIdentifier = this.getTrackedEntityAttributeValueAuditIdentifier(
          trackedEntityAttributeValueAuditItem
        );
        if (trackedEntityAttributeValueAuditCollectionIdentifiers.includes(trackedEntityAttributeValueAuditIdentifier)) {
          return false;
        }
        trackedEntityAttributeValueAuditCollectionIdentifiers.push(trackedEntityAttributeValueAuditIdentifier);
        return true;
      });
      return [...trackedEntityAttributeValueAuditsToAdd, ...trackedEntityAttributeValueAuditCollection];
    }
    return trackedEntityAttributeValueAuditCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityAttributeValueAudit | NewTrackedEntityAttributeValueAudit | PartialUpdateTrackedEntityAttributeValueAudit
  >(trackedEntityAttributeValueAudit: T): RestOf<T> {
    return {
      ...trackedEntityAttributeValueAudit,
      created: trackedEntityAttributeValueAudit.created?.toJSON() ?? null,
      updated: trackedEntityAttributeValueAudit.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(
    restTrackedEntityAttributeValueAudit: RestTrackedEntityAttributeValueAudit
  ): ITrackedEntityAttributeValueAudit {
    return {
      ...restTrackedEntityAttributeValueAudit,
      created: restTrackedEntityAttributeValueAudit.created ? dayjs(restTrackedEntityAttributeValueAudit.created) : undefined,
      updated: restTrackedEntityAttributeValueAudit.updated ? dayjs(restTrackedEntityAttributeValueAudit.updated) : undefined,
    };
  }

  protected convertResponseFromServer(
    res: HttpResponse<RestTrackedEntityAttributeValueAudit>
  ): HttpResponse<ITrackedEntityAttributeValueAudit> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestTrackedEntityAttributeValueAudit[]>
  ): HttpResponse<ITrackedEntityAttributeValueAudit[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
