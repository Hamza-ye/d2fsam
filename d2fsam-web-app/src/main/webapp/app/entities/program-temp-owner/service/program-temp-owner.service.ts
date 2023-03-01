import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramTempOwner, NewProgramTempOwner } from '../program-temp-owner.model';

export type PartialUpdateProgramTempOwner = Partial<IProgramTempOwner> & Pick<IProgramTempOwner, 'id'>;

type RestOf<T extends IProgramTempOwner | NewProgramTempOwner> = Omit<T, 'validTill'> & {
  validTill?: string | null;
};

export type RestProgramTempOwner = RestOf<IProgramTempOwner>;

export type NewRestProgramTempOwner = RestOf<NewProgramTempOwner>;

export type PartialUpdateRestProgramTempOwner = RestOf<PartialUpdateProgramTempOwner>;

export type EntityResponseType = HttpResponse<IProgramTempOwner>;
export type EntityArrayResponseType = HttpResponse<IProgramTempOwner[]>;

@Injectable({ providedIn: 'root' })
export class ProgramTempOwnerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-temp-owners');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programTempOwner: NewProgramTempOwner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTempOwner);
    return this.http
      .post<RestProgramTempOwner>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programTempOwner: IProgramTempOwner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTempOwner);
    return this.http
      .put<RestProgramTempOwner>(`${this.resourceUrl}/${this.getProgramTempOwnerIdentifier(programTempOwner)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programTempOwner: PartialUpdateProgramTempOwner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTempOwner);
    return this.http
      .patch<RestProgramTempOwner>(`${this.resourceUrl}/${this.getProgramTempOwnerIdentifier(programTempOwner)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramTempOwner>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramTempOwner[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramTempOwnerIdentifier(programTempOwner: Pick<IProgramTempOwner, 'id'>): number {
    return programTempOwner.id;
  }

  compareProgramTempOwner(o1: Pick<IProgramTempOwner, 'id'> | null, o2: Pick<IProgramTempOwner, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgramTempOwnerIdentifier(o1) === this.getProgramTempOwnerIdentifier(o2) : o1 === o2;
  }

  addProgramTempOwnerToCollectionIfMissing<Type extends Pick<IProgramTempOwner, 'id'>>(
    programTempOwnerCollection: Type[],
    ...programTempOwnersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programTempOwners: Type[] = programTempOwnersToCheck.filter(isPresent);
    if (programTempOwners.length > 0) {
      const programTempOwnerCollectionIdentifiers = programTempOwnerCollection.map(
        programTempOwnerItem => this.getProgramTempOwnerIdentifier(programTempOwnerItem)!
      );
      const programTempOwnersToAdd = programTempOwners.filter(programTempOwnerItem => {
        const programTempOwnerIdentifier = this.getProgramTempOwnerIdentifier(programTempOwnerItem);
        if (programTempOwnerCollectionIdentifiers.includes(programTempOwnerIdentifier)) {
          return false;
        }
        programTempOwnerCollectionIdentifiers.push(programTempOwnerIdentifier);
        return true;
      });
      return [...programTempOwnersToAdd, ...programTempOwnerCollection];
    }
    return programTempOwnerCollection;
  }

  protected convertDateFromClient<T extends IProgramTempOwner | NewProgramTempOwner | PartialUpdateProgramTempOwner>(
    programTempOwner: T
  ): RestOf<T> {
    return {
      ...programTempOwner,
      validTill: programTempOwner.validTill?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramTempOwner: RestProgramTempOwner): IProgramTempOwner {
    return {
      ...restProgramTempOwner,
      validTill: restProgramTempOwner.validTill ? dayjs(restProgramTempOwner.validTill) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramTempOwner>): HttpResponse<IProgramTempOwner> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramTempOwner[]>): HttpResponse<IProgramTempOwner[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
