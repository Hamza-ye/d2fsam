import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAssignment } from '../assignment.model';
import { AssignmentService } from '../service/assignment.service';

@Injectable({ providedIn: 'root' })
export class AssignmentRoutingResolveService implements Resolve<IAssignment | null> {
  constructor(protected service: AssignmentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAssignment | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((assignment: HttpResponse<IAssignment>) => {
          if (assignment.body) {
            return of(assignment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
