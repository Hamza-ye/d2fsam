import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramTempOwnershipAudit, NewProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';

export type PartialUpdateProgramTempOwnershipAudit = Partial<IProgramTempOwnershipAudit> & Pick<IProgramTempOwnershipAudit, 'id'>;

type RestOf<T extends IProgramTempOwnershipAudit | NewProgramTempOwnershipAudit> = Omit<T, 'created'> & {
  created?: string | null;
};

export type RestProgramTempOwnershipAudit = RestOf<IProgramTempOwnershipAudit>;

export type NewRestProgramTempOwnershipAudit = RestOf<NewProgramTempOwnershipAudit>;

export type PartialUpdateRestProgramTempOwnershipAudit = RestOf<PartialUpdateProgramTempOwnershipAudit>;

export type EntityResponseType = HttpResponse<IProgramTempOwnershipAudit>;
export type EntityArrayResponseType = HttpResponse<IProgramTempOwnershipAudit[]>;

@Injectable({ providedIn: 'root' })
export class ProgramTempOwnershipAuditService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-temp-ownership-audits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programTempOwnershipAudit: NewProgramTempOwnershipAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTempOwnershipAudit);
    return this.http
      .post<RestProgramTempOwnershipAudit>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programTempOwnershipAudit: IProgramTempOwnershipAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTempOwnershipAudit);
    return this.http
      .put<RestProgramTempOwnershipAudit>(
        `${this.resourceUrl}/${this.getProgramTempOwnershipAuditIdentifier(programTempOwnershipAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programTempOwnershipAudit: PartialUpdateProgramTempOwnershipAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTempOwnershipAudit);
    return this.http
      .patch<RestProgramTempOwnershipAudit>(
        `${this.resourceUrl}/${this.getProgramTempOwnershipAuditIdentifier(programTempOwnershipAudit)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramTempOwnershipAudit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramTempOwnershipAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramTempOwnershipAuditIdentifier(programTempOwnershipAudit: Pick<IProgramTempOwnershipAudit, 'id'>): number {
    return programTempOwnershipAudit.id;
  }

  compareProgramTempOwnershipAudit(
    o1: Pick<IProgramTempOwnershipAudit, 'id'> | null,
    o2: Pick<IProgramTempOwnershipAudit, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getProgramTempOwnershipAuditIdentifier(o1) === this.getProgramTempOwnershipAuditIdentifier(o2) : o1 === o2;
  }

  addProgramTempOwnershipAuditToCollectionIfMissing<Type extends Pick<IProgramTempOwnershipAudit, 'id'>>(
    programTempOwnershipAuditCollection: Type[],
    ...programTempOwnershipAuditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programTempOwnershipAudits: Type[] = programTempOwnershipAuditsToCheck.filter(isPresent);
    if (programTempOwnershipAudits.length > 0) {
      const programTempOwnershipAuditCollectionIdentifiers = programTempOwnershipAuditCollection.map(
        programTempOwnershipAuditItem => this.getProgramTempOwnershipAuditIdentifier(programTempOwnershipAuditItem)!
      );
      const programTempOwnershipAuditsToAdd = programTempOwnershipAudits.filter(programTempOwnershipAuditItem => {
        const programTempOwnershipAuditIdentifier = this.getProgramTempOwnershipAuditIdentifier(programTempOwnershipAuditItem);
        if (programTempOwnershipAuditCollectionIdentifiers.includes(programTempOwnershipAuditIdentifier)) {
          return false;
        }
        programTempOwnershipAuditCollectionIdentifiers.push(programTempOwnershipAuditIdentifier);
        return true;
      });
      return [...programTempOwnershipAuditsToAdd, ...programTempOwnershipAuditCollection];
    }
    return programTempOwnershipAuditCollection;
  }

  protected convertDateFromClient<
    T extends IProgramTempOwnershipAudit | NewProgramTempOwnershipAudit | PartialUpdateProgramTempOwnershipAudit
  >(programTempOwnershipAudit: T): RestOf<T> {
    return {
      ...programTempOwnershipAudit,
      created: programTempOwnershipAudit.created?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramTempOwnershipAudit: RestProgramTempOwnershipAudit): IProgramTempOwnershipAudit {
    return {
      ...restProgramTempOwnershipAudit,
      created: restProgramTempOwnershipAudit.created ? dayjs(restProgramTempOwnershipAudit.created) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramTempOwnershipAudit>): HttpResponse<IProgramTempOwnershipAudit> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramTempOwnershipAudit[]>): HttpResponse<IProgramTempOwnershipAudit[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
