import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAssignment, NewAssignment } from '../assignment.model';

export type PartialUpdateAssignment = Partial<IAssignment> & Pick<IAssignment, 'id'>;

type RestOf<T extends IAssignment | NewAssignment> = Omit<T, 'created' | 'updated' | 'startDate' | 'deletedAt'> & {
  created?: string | null;
  updated?: string | null;
  startDate?: string | null;
  deletedAt?: string | null;
};

export type RestAssignment = RestOf<IAssignment>;

export type NewRestAssignment = RestOf<NewAssignment>;

export type PartialUpdateRestAssignment = RestOf<PartialUpdateAssignment>;

export type EntityResponseType = HttpResponse<IAssignment>;
export type EntityArrayResponseType = HttpResponse<IAssignment[]>;

@Injectable({ providedIn: 'root' })
export class AssignmentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/assignments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(assignment: NewAssignment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(assignment);
    return this.http
      .post<RestAssignment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(assignment: IAssignment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(assignment);
    return this.http
      .put<RestAssignment>(`${this.resourceUrl}/${this.getAssignmentIdentifier(assignment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(assignment: PartialUpdateAssignment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(assignment);
    return this.http
      .patch<RestAssignment>(`${this.resourceUrl}/${this.getAssignmentIdentifier(assignment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAssignment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAssignment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAssignmentIdentifier(assignment: Pick<IAssignment, 'id'>): number {
    return assignment.id;
  }

  compareAssignment(o1: Pick<IAssignment, 'id'> | null, o2: Pick<IAssignment, 'id'> | null): boolean {
    return o1 && o2 ? this.getAssignmentIdentifier(o1) === this.getAssignmentIdentifier(o2) : o1 === o2;
  }

  addAssignmentToCollectionIfMissing<Type extends Pick<IAssignment, 'id'>>(
    assignmentCollection: Type[],
    ...assignmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const assignments: Type[] = assignmentsToCheck.filter(isPresent);
    if (assignments.length > 0) {
      const assignmentCollectionIdentifiers = assignmentCollection.map(assignmentItem => this.getAssignmentIdentifier(assignmentItem)!);
      const assignmentsToAdd = assignments.filter(assignmentItem => {
        const assignmentIdentifier = this.getAssignmentIdentifier(assignmentItem);
        if (assignmentCollectionIdentifiers.includes(assignmentIdentifier)) {
          return false;
        }
        assignmentCollectionIdentifiers.push(assignmentIdentifier);
        return true;
      });
      return [...assignmentsToAdd, ...assignmentCollection];
    }
    return assignmentCollection;
  }

  protected convertDateFromClient<T extends IAssignment | NewAssignment | PartialUpdateAssignment>(assignment: T): RestOf<T> {
    return {
      ...assignment,
      created: assignment.created?.toJSON() ?? null,
      updated: assignment.updated?.toJSON() ?? null,
      startDate: assignment.startDate?.format(DATE_FORMAT) ?? null,
      deletedAt: assignment.deletedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAssignment: RestAssignment): IAssignment {
    return {
      ...restAssignment,
      created: restAssignment.created ? dayjs(restAssignment.created) : undefined,
      updated: restAssignment.updated ? dayjs(restAssignment.updated) : undefined,
      startDate: restAssignment.startDate ? dayjs(restAssignment.startDate) : undefined,
      deletedAt: restAssignment.deletedAt ? dayjs(restAssignment.deletedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAssignment>): HttpResponse<IAssignment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAssignment[]>): HttpResponse<IAssignment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
