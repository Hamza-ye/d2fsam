import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDataElement, NewDataElement } from '../data-element.model';

export type PartialUpdateDataElement = Partial<IDataElement> & Pick<IDataElement, 'id'>;

type RestOf<T extends IDataElement | NewDataElement> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestDataElement = RestOf<IDataElement>;

export type NewRestDataElement = RestOf<NewDataElement>;

export type PartialUpdateRestDataElement = RestOf<PartialUpdateDataElement>;

export type EntityResponseType = HttpResponse<IDataElement>;
export type EntityArrayResponseType = HttpResponse<IDataElement[]>;

@Injectable({ providedIn: 'root' })
export class DataElementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/data-elements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(dataElement: NewDataElement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataElement);
    return this.http
      .post<RestDataElement>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dataElement: IDataElement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataElement);
    return this.http
      .put<RestDataElement>(`${this.resourceUrl}/${this.getDataElementIdentifier(dataElement)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dataElement: PartialUpdateDataElement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dataElement);
    return this.http
      .patch<RestDataElement>(`${this.resourceUrl}/${this.getDataElementIdentifier(dataElement)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDataElement>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDataElement[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDataElementIdentifier(dataElement: Pick<IDataElement, 'id'>): number {
    return dataElement.id;
  }

  compareDataElement(o1: Pick<IDataElement, 'id'> | null, o2: Pick<IDataElement, 'id'> | null): boolean {
    return o1 && o2 ? this.getDataElementIdentifier(o1) === this.getDataElementIdentifier(o2) : o1 === o2;
  }

  addDataElementToCollectionIfMissing<Type extends Pick<IDataElement, 'id'>>(
    dataElementCollection: Type[],
    ...dataElementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dataElements: Type[] = dataElementsToCheck.filter(isPresent);
    if (dataElements.length > 0) {
      const dataElementCollectionIdentifiers = dataElementCollection.map(
        dataElementItem => this.getDataElementIdentifier(dataElementItem)!
      );
      const dataElementsToAdd = dataElements.filter(dataElementItem => {
        const dataElementIdentifier = this.getDataElementIdentifier(dataElementItem);
        if (dataElementCollectionIdentifiers.includes(dataElementIdentifier)) {
          return false;
        }
        dataElementCollectionIdentifiers.push(dataElementIdentifier);
        return true;
      });
      return [...dataElementsToAdd, ...dataElementCollection];
    }
    return dataElementCollection;
  }

  protected convertDateFromClient<T extends IDataElement | NewDataElement | PartialUpdateDataElement>(dataElement: T): RestOf<T> {
    return {
      ...dataElement,
      created: dataElement.created?.toJSON() ?? null,
      updated: dataElement.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDataElement: RestDataElement): IDataElement {
    return {
      ...restDataElement,
      created: restDataElement.created ? dayjs(restDataElement.created) : undefined,
      updated: restDataElement.updated ? dayjs(restDataElement.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDataElement>): HttpResponse<IDataElement> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDataElement[]>): HttpResponse<IDataElement[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
