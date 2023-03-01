import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackedEntityInstanceAudit, NewTrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';

export type PartialUpdateTrackedEntityInstanceAudit = Partial<ITrackedEntityInstanceAudit> & Pick<ITrackedEntityInstanceAudit, 'id'>;

type RestOf<T extends ITrackedEntityInstanceAudit | NewTrackedEntityInstanceAudit> = Omit<T, 'created'> & {
  created?: string | null;
};

export type RestTrackedEntityInstanceAudit = RestOf<ITrackedEntityInstanceAudit>;

export type NewRestTrackedEntityInstanceAudit = RestOf<NewTrackedEntityInstanceAudit>;

export type PartialUpdateRestTrackedEntityInstanceAudit = RestOf<PartialUpdateTrackedEntityInstanceAudit>;

export type EntityResponseType = HttpResponse<ITrackedEntityInstanceAudit>;
export type EntityArrayResponseType = HttpResponse<ITrackedEntityInstanceAudit[]>;

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceAuditService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracked-entity-instance-audits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(trackedEntityInstanceAudit: NewTrackedEntityInstanceAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstanceAudit);
    return this.http
      .post<RestTrackedEntityInstanceAudit>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trackedEntityInstanceAudit: ITrackedEntityInstanceAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstanceAudit);
    return this.http
      .put<RestTrackedEntityInstanceAudit>(
        `${this.resourceUrl}/${this.getTrackedEntityInstanceAuditIdentifier(trackedEntityInstanceAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trackedEntityInstanceAudit: PartialUpdateTrackedEntityInstanceAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trackedEntityInstanceAudit);
    return this.http
      .patch<RestTrackedEntityInstanceAudit>(
        `${this.resourceUrl}/${this.getTrackedEntityInstanceAuditIdentifier(trackedEntityInstanceAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrackedEntityInstanceAudit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrackedEntityInstanceAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackedEntityInstanceAuditIdentifier(trackedEntityInstanceAudit: Pick<ITrackedEntityInstanceAudit, 'id'>): number {
    return trackedEntityInstanceAudit.id;
  }

  compareTrackedEntityInstanceAudit(
    o1: Pick<ITrackedEntityInstanceAudit, 'id'> | null,
    o2: Pick<ITrackedEntityInstanceAudit, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getTrackedEntityInstanceAuditIdentifier(o1) === this.getTrackedEntityInstanceAuditIdentifier(o2) : o1 === o2;
  }

  addTrackedEntityInstanceAuditToCollectionIfMissing<Type extends Pick<ITrackedEntityInstanceAudit, 'id'>>(
    trackedEntityInstanceAuditCollection: Type[],
    ...trackedEntityInstanceAuditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trackedEntityInstanceAudits: Type[] = trackedEntityInstanceAuditsToCheck.filter(isPresent);
    if (trackedEntityInstanceAudits.length > 0) {
      const trackedEntityInstanceAuditCollectionIdentifiers = trackedEntityInstanceAuditCollection.map(
        trackedEntityInstanceAuditItem => this.getTrackedEntityInstanceAuditIdentifier(trackedEntityInstanceAuditItem)!
      );
      const trackedEntityInstanceAuditsToAdd = trackedEntityInstanceAudits.filter(trackedEntityInstanceAuditItem => {
        const trackedEntityInstanceAuditIdentifier = this.getTrackedEntityInstanceAuditIdentifier(trackedEntityInstanceAuditItem);
        if (trackedEntityInstanceAuditCollectionIdentifiers.includes(trackedEntityInstanceAuditIdentifier)) {
          return false;
        }
        trackedEntityInstanceAuditCollectionIdentifiers.push(trackedEntityInstanceAuditIdentifier);
        return true;
      });
      return [...trackedEntityInstanceAuditsToAdd, ...trackedEntityInstanceAuditCollection];
    }
    return trackedEntityInstanceAuditCollection;
  }

  protected convertDateFromClient<
    T extends ITrackedEntityInstanceAudit | NewTrackedEntityInstanceAudit | PartialUpdateTrackedEntityInstanceAudit
  >(trackedEntityInstanceAudit: T): RestOf<T> {
    return {
      ...trackedEntityInstanceAudit,
      created: trackedEntityInstanceAudit.created?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTrackedEntityInstanceAudit: RestTrackedEntityInstanceAudit): ITrackedEntityInstanceAudit {
    return {
      ...restTrackedEntityInstanceAudit,
      created: restTrackedEntityInstanceAudit.created ? dayjs(restTrackedEntityInstanceAudit.created) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrackedEntityInstanceAudit>): HttpResponse<ITrackedEntityInstanceAudit> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestTrackedEntityInstanceAudit[]>
  ): HttpResponse<ITrackedEntityInstanceAudit[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
