import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDatastoreEntry, NewDatastoreEntry } from '../datastore-entry.model';

export type PartialUpdateDatastoreEntry = Partial<IDatastoreEntry> & Pick<IDatastoreEntry, 'id'>;

type RestOf<T extends IDatastoreEntry | NewDatastoreEntry> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

export type RestDatastoreEntry = RestOf<IDatastoreEntry>;

export type NewRestDatastoreEntry = RestOf<NewDatastoreEntry>;

export type PartialUpdateRestDatastoreEntry = RestOf<PartialUpdateDatastoreEntry>;

export type EntityResponseType = HttpResponse<IDatastoreEntry>;
export type EntityArrayResponseType = HttpResponse<IDatastoreEntry[]>;

@Injectable({ providedIn: 'root' })
export class DatastoreEntryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/datastore-entries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(datastoreEntry: NewDatastoreEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(datastoreEntry);
    return this.http
      .post<RestDatastoreEntry>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(datastoreEntry: IDatastoreEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(datastoreEntry);
    return this.http
      .put<RestDatastoreEntry>(`${this.resourceUrl}/${this.getDatastoreEntryIdentifier(datastoreEntry)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(datastoreEntry: PartialUpdateDatastoreEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(datastoreEntry);
    return this.http
      .patch<RestDatastoreEntry>(`${this.resourceUrl}/${this.getDatastoreEntryIdentifier(datastoreEntry)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDatastoreEntry>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDatastoreEntry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDatastoreEntryIdentifier(datastoreEntry: Pick<IDatastoreEntry, 'id'>): number {
    return datastoreEntry.id;
  }

  compareDatastoreEntry(o1: Pick<IDatastoreEntry, 'id'> | null, o2: Pick<IDatastoreEntry, 'id'> | null): boolean {
    return o1 && o2 ? this.getDatastoreEntryIdentifier(o1) === this.getDatastoreEntryIdentifier(o2) : o1 === o2;
  }

  addDatastoreEntryToCollectionIfMissing<Type extends Pick<IDatastoreEntry, 'id'>>(
    datastoreEntryCollection: Type[],
    ...datastoreEntriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const datastoreEntries: Type[] = datastoreEntriesToCheck.filter(isPresent);
    if (datastoreEntries.length > 0) {
      const datastoreEntryCollectionIdentifiers = datastoreEntryCollection.map(
        datastoreEntryItem => this.getDatastoreEntryIdentifier(datastoreEntryItem)!
      );
      const datastoreEntriesToAdd = datastoreEntries.filter(datastoreEntryItem => {
        const datastoreEntryIdentifier = this.getDatastoreEntryIdentifier(datastoreEntryItem);
        if (datastoreEntryCollectionIdentifiers.includes(datastoreEntryIdentifier)) {
          return false;
        }
        datastoreEntryCollectionIdentifiers.push(datastoreEntryIdentifier);
        return true;
      });
      return [...datastoreEntriesToAdd, ...datastoreEntryCollection];
    }
    return datastoreEntryCollection;
  }

  protected convertDateFromClient<T extends IDatastoreEntry | NewDatastoreEntry | PartialUpdateDatastoreEntry>(
    datastoreEntry: T
  ): RestOf<T> {
    return {
      ...datastoreEntry,
      created: datastoreEntry.created?.toJSON() ?? null,
      updated: datastoreEntry.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDatastoreEntry: RestDatastoreEntry): IDatastoreEntry {
    return {
      ...restDatastoreEntry,
      created: restDatastoreEntry.created ? dayjs(restDatastoreEntry.created) : undefined,
      updated: restDatastoreEntry.updated ? dayjs(restDatastoreEntry.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDatastoreEntry>): HttpResponse<IDatastoreEntry> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDatastoreEntry[]>): HttpResponse<IDatastoreEntry[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
