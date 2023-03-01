import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProgramStageInstance, NewProgramStageInstance } from '../program-stage-instance.model';

export type PartialUpdateProgramStageInstance = Partial<IProgramStageInstance> & Pick<IProgramStageInstance, 'id'>;

type RestOf<T extends IProgramStageInstance | NewProgramStageInstance> = Omit<
  T,
  | 'created'
  | 'updated'
  | 'createdAtClient'
  | 'updatedAtClient'
  | 'lastSynchronized'
  | 'dueDate'
  | 'executionDate'
  | 'completedDate'
  | 'deletedAt'
> & {
  created?: string | null;
  updated?: string | null;
  createdAtClient?: string | null;
  updatedAtClient?: string | null;
  lastSynchronized?: string | null;
  dueDate?: string | null;
  executionDate?: string | null;
  completedDate?: string | null;
  deletedAt?: string | null;
};

export type RestProgramStageInstance = RestOf<IProgramStageInstance>;

export type NewRestProgramStageInstance = RestOf<NewProgramStageInstance>;

export type PartialUpdateRestProgramStageInstance = RestOf<PartialUpdateProgramStageInstance>;

export type EntityResponseType = HttpResponse<IProgramStageInstance>;
export type EntityArrayResponseType = HttpResponse<IProgramStageInstance[]>;

@Injectable({ providedIn: 'root' })
export class ProgramStageInstanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/program-stage-instances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(programStageInstance: NewProgramStageInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageInstance);
    return this.http
      .post<RestProgramStageInstance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(programStageInstance: IProgramStageInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageInstance);
    return this.http
      .put<RestProgramStageInstance>(`${this.resourceUrl}/${this.getProgramStageInstanceIdentifier(programStageInstance)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(programStageInstance: PartialUpdateProgramStageInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(programStageInstance);
    return this.http
      .patch<RestProgramStageInstance>(`${this.resourceUrl}/${this.getProgramStageInstanceIdentifier(programStageInstance)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgramStageInstance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgramStageInstance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProgramStageInstanceIdentifier(programStageInstance: Pick<IProgramStageInstance, 'id'>): number {
    return programStageInstance.id;
  }

  compareProgramStageInstance(o1: Pick<IProgramStageInstance, 'id'> | null, o2: Pick<IProgramStageInstance, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgramStageInstanceIdentifier(o1) === this.getProgramStageInstanceIdentifier(o2) : o1 === o2;
  }

  addProgramStageInstanceToCollectionIfMissing<Type extends Pick<IProgramStageInstance, 'id'>>(
    programStageInstanceCollection: Type[],
    ...programStageInstancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const programStageInstances: Type[] = programStageInstancesToCheck.filter(isPresent);
    if (programStageInstances.length > 0) {
      const programStageInstanceCollectionIdentifiers = programStageInstanceCollection.map(
        programStageInstanceItem => this.getProgramStageInstanceIdentifier(programStageInstanceItem)!
      );
      const programStageInstancesToAdd = programStageInstances.filter(programStageInstanceItem => {
        const programStageInstanceIdentifier = this.getProgramStageInstanceIdentifier(programStageInstanceItem);
        if (programStageInstanceCollectionIdentifiers.includes(programStageInstanceIdentifier)) {
          return false;
        }
        programStageInstanceCollectionIdentifiers.push(programStageInstanceIdentifier);
        return true;
      });
      return [...programStageInstancesToAdd, ...programStageInstanceCollection];
    }
    return programStageInstanceCollection;
  }

  protected convertDateFromClient<T extends IProgramStageInstance | NewProgramStageInstance | PartialUpdateProgramStageInstance>(
    programStageInstance: T
  ): RestOf<T> {
    return {
      ...programStageInstance,
      created: programStageInstance.created?.toJSON() ?? null,
      updated: programStageInstance.updated?.toJSON() ?? null,
      createdAtClient: programStageInstance.createdAtClient?.toJSON() ?? null,
      updatedAtClient: programStageInstance.updatedAtClient?.toJSON() ?? null,
      lastSynchronized: programStageInstance.lastSynchronized?.toJSON() ?? null,
      dueDate: programStageInstance.dueDate?.toJSON() ?? null,
      executionDate: programStageInstance.executionDate?.toJSON() ?? null,
      completedDate: programStageInstance.completedDate?.toJSON() ?? null,
      deletedAt: programStageInstance.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProgramStageInstance: RestProgramStageInstance): IProgramStageInstance {
    return {
      ...restProgramStageInstance,
      created: restProgramStageInstance.created ? dayjs(restProgramStageInstance.created) : undefined,
      updated: restProgramStageInstance.updated ? dayjs(restProgramStageInstance.updated) : undefined,
      createdAtClient: restProgramStageInstance.createdAtClient ? dayjs(restProgramStageInstance.createdAtClient) : undefined,
      updatedAtClient: restProgramStageInstance.updatedAtClient ? dayjs(restProgramStageInstance.updatedAtClient) : undefined,
      lastSynchronized: restProgramStageInstance.lastSynchronized ? dayjs(restProgramStageInstance.lastSynchronized) : undefined,
      dueDate: restProgramStageInstance.dueDate ? dayjs(restProgramStageInstance.dueDate) : undefined,
      executionDate: restProgramStageInstance.executionDate ? dayjs(restProgramStageInstance.executionDate) : undefined,
      completedDate: restProgramStageInstance.completedDate ? dayjs(restProgramStageInstance.completedDate) : undefined,
      deletedAt: restProgramStageInstance.deletedAt ? dayjs(restProgramStageInstance.deletedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgramStageInstance>): HttpResponse<IProgramStageInstance> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgramStageInstance[]>): HttpResponse<IProgramStageInstance[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
