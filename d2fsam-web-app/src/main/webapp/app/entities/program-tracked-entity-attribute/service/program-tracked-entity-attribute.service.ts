import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramTrackedEntityAttribute, NewProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';

export type PartialUpdateProgramTrackedEntityAttribute = Partial<IProgramTrackedEntityAttribute> &
  Pick<IProgramTrackedEntityAttribute, 'id'>;

type RestOf<T extends IProgramTrackedEntityAttribute | NewProgramTrackedEntityAttribute> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestProgramTrackedEntityAttribute = RestOf<IProgramTrackedEntityAttribute>;

export type NewRestProgramTrackedEntityAttribute = RestOf<NewProgramTrackedEntityAttribute>;

export type PartialUpdateRestProgramTrackedEntityAttribute = RestOf<PartialUpdateProgramTrackedEntityAttribute>;

export type EntityResponseType = HttpResponse<IProgramTrackedEntityAttribute>;
export type EntityArrayResponseType = HttpResponse<IProgramTrackedEntityAttribute[]>;

@Injectable({ providedIn: 'root' })
export class ProgramTrackedEntityAttributeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-tracked-entity-attributes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programTrackedEntityAttribute: NewProgramTrackedEntityAttribute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTrackedEntityAttribute);
    return this.http
      .post<RestProgramTrackedEntityAttribute>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programTrackedEntityAttribute: IProgramTrackedEntityAttribute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTrackedEntityAttribute);
    return this.http
      .put<RestProgramTrackedEntityAttribute>(
        `${this.resourceUrl}/${this.getProgramTrackedEntityAttributeIdentifier(programTrackedEntityAttribute)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programTrackedEntityAttribute: PartialUpdateProgramTrackedEntityAttribute): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programTrackedEntityAttribute);
    return this.http
      .patch<RestProgramTrackedEntityAttribute>(
        `${this.resourceUrl}/${this.getProgramTrackedEntityAttributeIdentifier(programTrackedEntityAttribute)}`,
        copy,
        { observe: 'response' }
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramTrackedEntityAttribute>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramTrackedEntityAttribute[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramTrackedEntityAttributeIdentifier(programTrackedEntityAttribute: Pick<IProgramTrackedEntityAttribute, 'id'>): number {
    return programTrackedEntityAttribute.id;
  }

  compareProgramTrackedEntityAttribute(
    o1: Pick<IProgramTrackedEntityAttribute, 'id'> | null,
    o2: Pick<IProgramTrackedEntityAttribute, 'id'> | null
  ): boolean {
    return o1 && o2
      ? this.getProgramTrackedEntityAttributeIdentifier(o1) === this.getProgramTrackedEntityAttributeIdentifier(o2)
      : o1 === o2;
  }

  addProgramTrackedEntityAttributeToCollectionIfMissing<Type extends Pick<IProgramTrackedEntityAttribute, 'id'>>(
    programTrackedEntityAttributeCollection: Type[],
    ...programTrackedEntityAttributesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programTrackedEntityAttributes: Type[] = programTrackedEntityAttributesToCheck.filter(isPresent);
    if (programTrackedEntityAttributes.length > 0) {
      const programTrackedEntityAttributeCollectionIdentifiers = programTrackedEntityAttributeCollection.map(
        programTrackedEntityAttributeItem => this.getProgramTrackedEntityAttributeIdentifier(programTrackedEntityAttributeItem)!
      );
      const programTrackedEntityAttributesToAdd = programTrackedEntityAttributes.filter(programTrackedEntityAttributeItem => {
        const programTrackedEntityAttributeIdentifier = this.getProgramTrackedEntityAttributeIdentifier(programTrackedEntityAttributeItem);
        if (programTrackedEntityAttributeCollectionIdentifiers.includes(programTrackedEntityAttributeIdentifier)) {
          return false;
        }
        programTrackedEntityAttributeCollectionIdentifiers.push(programTrackedEntityAttributeIdentifier);
        return true;
      });
      return [...programTrackedEntityAttributesToAdd, ...programTrackedEntityAttributeCollection];
    }
    return programTrackedEntityAttributeCollection;
  }

  protected convertDateFromClient<
    T extends IProgramTrackedEntityAttribute | NewProgramTrackedEntityAttribute | PartialUpdateProgramTrackedEntityAttribute
  >(programTrackedEntityAttribute: T): RestOf<T> {
    return {
      ...programTrackedEntityAttribute,
      created: programTrackedEntityAttribute.created?.toJSON() ?? null,
      updated: programTrackedEntityAttribute.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramTrackedEntityAttribute: RestProgramTrackedEntityAttribute): IProgramTrackedEntityAttribute {
    return {
      ...restProgramTrackedEntityAttribute,
      created: restProgramTrackedEntityAttribute.created ? dayjs(restProgramTrackedEntityAttribute.created) : undefined,
      updated: restProgramTrackedEntityAttribute.updated ? dayjs(restProgramTrackedEntityAttribute.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramTrackedEntityAttribute>): HttpResponse<IProgramTrackedEntityAttribute> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(
    res: HttpResponse<RestProgramTrackedEntityAttribute[]>
  ): HttpResponse<IProgramTrackedEntityAttribute[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
