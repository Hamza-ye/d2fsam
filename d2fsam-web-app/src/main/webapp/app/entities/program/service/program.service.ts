import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgram, NewProgram } from '../program.model';

export type PartialUpdateProgram = Partial<IProgram> & Pick<IProgram, 'id'>;

type RestOf<T extends IProgram | NewProgram> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestProgram = RestOf<IProgram>;

export type NewRestProgram = RestOf<NewProgram>;

export type PartialUpdateRestProgram = RestOf<PartialUpdateProgram>;

export type EntityResponseType = HttpResponse<IProgram>;
export type EntityArrayResponseType = HttpResponse<IProgram[]>;

@Injectable({ providedIn: 'root' })
export class ProgramService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/programs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(program: NewProgram): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(program);
    return this.http
      .post<RestProgram>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(program: IProgram): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(program);
    return this.http
      .put<RestProgram>(`${this.resourceUrl}/${this.getProgramIdentifier(program)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(program: PartialUpdateProgram): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(program);
    return this.http
      .patch<RestProgram>(`${this.resourceUrl}/${this.getProgramIdentifier(program)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgram>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgram[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramIdentifier(program: Pick<IProgram, 'id'>): number {
    return program.id;
  }

  compareProgram(o1: Pick<IProgram, 'id'> | null, o2: Pick<IProgram, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgramIdentifier(o1) === this.getProgramIdentifier(o2) : o1 === o2;
  }

  addProgramToCollectionIfMissing<Type extends Pick<IProgram, 'id'>>(
    programCollection: Type[],
    ...programsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programs: Type[] = programsToCheck.filter(isPresent);
    if (programs.length > 0) {
      const programCollectionIdentifiers = programCollection.map(programItem => this.getProgramIdentifier(programItem)!);
      const programsToAdd = programs.filter(programItem => {
        const programIdentifier = this.getProgramIdentifier(programItem);
        if (programCollectionIdentifiers.includes(programIdentifier)) {
          return false;
        }
        programCollectionIdentifiers.push(programIdentifier);
        return true;
      });
      return [...programsToAdd, ...programCollection];
    }
    return programCollection;
  }

  protected convertDateFromClient<T extends IProgram | NewProgram | PartialUpdateProgram>(program: T): RestOf<T> {
    return {
      ...program,
      created: program.created?.toJSON() ?? null,
      updated: program.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgram: RestProgram): IProgram {
    return {
      ...restProgram,
      created: restProgram.created ? dayjs(restProgram.created) : undefined,
      updated: restProgram.updated ? dayjs(restProgram.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgram>): HttpResponse<IProgram> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgram[]>): HttpResponse<IProgram[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
