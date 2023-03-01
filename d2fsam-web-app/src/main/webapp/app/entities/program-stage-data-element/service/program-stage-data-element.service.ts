import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramStageDataElement, NewProgramStageDataElement } from '../program-stage-data-element.model';

export type PartialUpdateProgramStageDataElement = Partial<IProgramStageDataElement> & Pick<IProgramStageDataElement, 'id'>;

type RestOf<T extends IProgramStageDataElement | NewProgramStageDataElement> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestProgramStageDataElement = RestOf<IProgramStageDataElement>;

export type NewRestProgramStageDataElement = RestOf<NewProgramStageDataElement>;

export type PartialUpdateRestProgramStageDataElement = RestOf<PartialUpdateProgramStageDataElement>;

export type EntityResponseType = HttpResponse<IProgramStageDataElement>;
export type EntityArrayResponseType = HttpResponse<IProgramStageDataElement[]>;

@Injectable({ providedIn: 'root' })
export class ProgramStageDataElementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-stage-data-elements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programStageDataElement: NewProgramStageDataElement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageDataElement);
    return this.http
      .post<RestProgramStageDataElement>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programStageDataElement: IProgramStageDataElement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageDataElement);
    return this.http
      .put<RestProgramStageDataElement>(`${this.resourceUrl}/${this.getProgramStageDataElementIdentifier(programStageDataElement)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programStageDataElement: PartialUpdateProgramStageDataElement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageDataElement);
    return this.http
      .patch<RestProgramStageDataElement>(
        `${this.resourceUrl}/${this.getProgramStageDataElementIdentifier(programStageDataElement)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramStageDataElement>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramStageDataElement[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramStageDataElementIdentifier(programStageDataElement: Pick<IProgramStageDataElement, 'id'>): number {
    return programStageDataElement.id;
  }

  compareProgramStageDataElement(
    o1: Pick<IProgramStageDataElement, 'id'> | null,
    o2: Pick<IProgramStageDataElement, 'id'> | null
  ): boolean {
    return o1 && o2 ? this.getProgramStageDataElementIdentifier(o1) === this.getProgramStageDataElementIdentifier(o2) : o1 === o2;
  }

  addProgramStageDataElementToCollectionIfMissing<Type extends Pick<IProgramStageDataElement, 'id'>>(
    programStageDataElementCollection: Type[],
    ...programStageDataElementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programStageDataElements: Type[] = programStageDataElementsToCheck.filter(isPresent);
    if (programStageDataElements.length > 0) {
      const programStageDataElementCollectionIdentifiers = programStageDataElementCollection.map(
        programStageDataElementItem => this.getProgramStageDataElementIdentifier(programStageDataElementItem)!
      );
      const programStageDataElementsToAdd = programStageDataElements.filter(programStageDataElementItem => {
        const programStageDataElementIdentifier = this.getProgramStageDataElementIdentifier(programStageDataElementItem);
        if (programStageDataElementCollectionIdentifiers.includes(programStageDataElementIdentifier)) {
          return false;
        }
        programStageDataElementCollectionIdentifiers.push(programStageDataElementIdentifier);
        return true;
      });
      return [...programStageDataElementsToAdd, ...programStageDataElementCollection];
    }
    return programStageDataElementCollection;
  }

  protected convertDateFromClient<T extends IProgramStageDataElement | NewProgramStageDataElement | PartialUpdateProgramStageDataElement>(
    programStageDataElement: T
  ): RestOf<T> {
    return {
      ...programStageDataElement,
      created: programStageDataElement.created?.toJSON() ?? null,
      updated: programStageDataElement.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramStageDataElement: RestProgramStageDataElement): IProgramStageDataElement {
    return {
      ...restProgramStageDataElement,
      created: restProgramStageDataElement.created ? dayjs(restProgramStageDataElement.created) : undefined,
      updated: restProgramStageDataElement.updated ? dayjs(restProgramStageDataElement.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramStageDataElement>): HttpResponse<IProgramStageDataElement> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramStageDataElement[]>): HttpResponse<IProgramStageDataElement[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
