import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramInstance, NewProgramInstance } from '../program-instance.model';

export type PartialUpdateProgramInstance = Partial<IProgramInstance> & Pick<IProgramInstance, 'id'>;

type RestOf<T extends IProgramInstance | NewProgramInstance> = Omit<
  T,
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'lastSynchronized'
  | 'incidentDate'
  | 'enrollmentDate'
  | 'endDate'
  | 'completedDate'
  | 'deletedAt'
> & {
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  lastSynchronized?: string | null;
  incidentDate?: string | null;
  enrollmentDate?: string | null;
  endDate?: string | null;
  completedDate?: string | null;
  deletedAt?: string | null;
};

export type RestProgramInstance = RestOf<IProgramInstance>;

export type NewRestProgramInstance = RestOf<NewProgramInstance>;

export type PartialUpdateRestProgramInstance = RestOf<PartialUpdateProgramInstance>;

export type EntityResponseType = HttpResponse<IProgramInstance>;
export type EntityArrayResponseType = HttpResponse<IProgramInstance[]>;

@Injectable({ providedIn: 'root' })
export class ProgramInstanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-instances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programInstance: NewProgramInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programInstance);
    return this.http
      .post<RestProgramInstance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programInstance: IProgramInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programInstance);
    return this.http
      .put<RestProgramInstance>(`${this.resourceUrl}/${this.getProgramInstanceIdentifier(programInstance)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programInstance: PartialUpdateProgramInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programInstance);
    return this.http
      .patch<RestProgramInstance>(`${this.resourceUrl}/${this.getProgramInstanceIdentifier(programInstance)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramInstance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramInstance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramInstanceIdentifier(programInstance: Pick<IProgramInstance, 'id'>): number {
    return programInstance.id;
  }

  compareProgramInstance(o1: Pick<IProgramInstance, 'id'> | null, o2: Pick<IProgramInstance, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgramInstanceIdentifier(o1) === this.getProgramInstanceIdentifier(o2) : o1 === o2;
  }

  addProgramInstanceToCollectionIfMissing<Type extends Pick<IProgramInstance, 'id'>>(
    programInstanceCollection: Type[],
    ...programInstancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programInstances: Type[] = programInstancesToCheck.filter(isPresent);
    if (programInstances.length > 0) {
      const programInstanceCollectionIdentifiers = programInstanceCollection.map(
        programInstanceItem => this.getProgramInstanceIdentifier(programInstanceItem)!
      );
      const programInstancesToAdd = programInstances.filter(programInstanceItem => {
        const programInstanceIdentifier = this.getProgramInstanceIdentifier(programInstanceItem);
        if (programInstanceCollectionIdentifiers.includes(programInstanceIdentifier)) {
          return false;
        }
        programInstanceCollectionIdentifiers.push(programInstanceIdentifier);
        return true;
      });
      return [...programInstancesToAdd, ...programInstanceCollection];
    }
    return programInstanceCollection;
  }

  protected convertDateFromClient<T extends IProgramInstance | NewProgramInstance | PartialUpdateProgramInstance>(
    programInstance: T
  ): RestOf<T> {
    return {
      ...programInstance,
      created: programInstance.created?.toJSON() ?? null,
      updated: programInstance.updated?.toJSON() ?? null,
      createdAtClient: programInstance.createdAtClient?.toJSON() ?? null,
      updatedAtClient: programInstance.updatedAtClient?.toJSON() ?? null,
      lastSynchronized: programInstance.lastSynchronized?.toJSON() ?? null,
      incidentDate: programInstance.incidentDate?.toJSON() ?? null,
      enrollmentDate: programInstance.enrollmentDate?.toJSON() ?? null,
      endDate: programInstance.endDate?.toJSON() ?? null,
      completedDate: programInstance.completedDate?.toJSON() ?? null,
      deletedAt: programInstance.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramInstance: RestProgramInstance): IProgramInstance {
    return {
      ...restProgramInstance,
      created: restProgramInstance.created ? dayjs(restProgramInstance.created) : undefined,
      updated: restProgramInstance.updated ? dayjs(restProgramInstance.updated) : undefined,
      createdAtClient: restProgramInstance.createdAtClient ? dayjs(restProgramInstance.createdAtClient) : undefined,
      updatedAtClient: restProgramInstance.updatedAtClient ? dayjs(restProgramInstance.updatedAtClient) : undefined,
      lastSynchronized: restProgramInstance.lastSynchronized ? dayjs(restProgramInstance.lastSynchronized) : undefined,
      incidentDate: restProgramInstance.incidentDate ? dayjs(restProgramInstance.incidentDate) : undefined,
      enrollmentDate: restProgramInstance.enrollmentDate ? dayjs(restProgramInstance.enrollmentDate) : undefined,
      endDate: restProgramInstance.endDate ? dayjs(restProgramInstance.endDate) : undefined,
      completedDate: restProgramInstance.completedDate ? dayjs(restProgramInstance.completedDate) : undefined,
      deletedAt: restProgramInstance.deletedAt ? dayjs(restProgramInstance.deletedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramInstance>): HttpResponse<IProgramInstance> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramInstance[]>): HttpResponse<IProgramInstance[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
