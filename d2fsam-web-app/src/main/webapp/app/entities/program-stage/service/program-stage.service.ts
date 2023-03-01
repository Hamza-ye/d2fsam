import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramStage, NewProgramStage } from '../program-stage.model';

export type PartialUpdateProgramStage = Partial<IProgramStage> & Pick<IProgramStage, 'id'>;

type RestOf<T extends IProgramStage | NewProgramStage> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestProgramStage = RestOf<IProgramStage>;

export type NewRestProgramStage = RestOf<NewProgramStage>;

export type PartialUpdateRestProgramStage = RestOf<PartialUpdateProgramStage>;

export type EntityResponseType = HttpResponse<IProgramStage>;
export type EntityArrayResponseType = HttpResponse<IProgramStage[]>;

@Injectable({ providedIn: 'root' })
export class ProgramStageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-stages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programStage: NewProgramStage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStage);
    return this.http
      .post<RestProgramStage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programStage: IProgramStage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStage);
    return this.http
      .put<RestProgramStage>(`${this.resourceUrl}/${this.getProgramStageIdentifier(programStage)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programStage: PartialUpdateProgramStage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStage);
    return this.http
      .patch<RestProgramStage>(`${this.resourceUrl}/${this.getProgramStageIdentifier(programStage)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramStage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramStage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramStageIdentifier(programStage: Pick<IProgramStage, 'id'>): number {
    return programStage.id;
  }

  compareProgramStage(o1: Pick<IProgramStage, 'id'> | null, o2: Pick<IProgramStage, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgramStageIdentifier(o1) === this.getProgramStageIdentifier(o2) : o1 === o2;
  }

  addProgramStageToCollectionIfMissing<Type extends Pick<IProgramStage, 'id'>>(
    programStageCollection: Type[],
    ...programStagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programStages: Type[] = programStagesToCheck.filter(isPresent);
    if (programStages.length > 0) {
      const programStageCollectionIdentifiers = programStageCollection.map(
        programStageItem => this.getProgramStageIdentifier(programStageItem)!
      );
      const programStagesToAdd = programStages.filter(programStageItem => {
        const programStageIdentifier = this.getProgramStageIdentifier(programStageItem);
        if (programStageCollectionIdentifiers.includes(programStageIdentifier)) {
          return false;
        }
        programStageCollectionIdentifiers.push(programStageIdentifier);
        return true;
      });
      return [...programStagesToAdd, ...programStageCollection];
    }
    return programStageCollection;
  }

  protected convertDateFromClient<T extends IProgramStage | NewProgramStage | PartialUpdateProgramStage>(programStage: T): RestOf<T> {
    return {
      ...programStage,
      created: programStage.created?.toJSON() ?? null,
      updated: programStage.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramStage: RestProgramStage): IProgramStage {
    return {
      ...restProgramStage,
      created: restProgramStage.created ? dayjs(restProgramStage.created) : undefined,
      updated: restProgramStage.updated ? dayjs(restProgramStage.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramStage>): HttpResponse<IProgramStage> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramStage[]>): HttpResponse<IProgramStage[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
