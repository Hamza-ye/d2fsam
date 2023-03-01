import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRelationshipConstraint } from '../relationship-constraint.model';
import { RelationshipConstraintService } from '../service/relationship-constraint.service';

@Injectable({ providedIn: 'root' })
export class RelationshipConstraintRoutingResolveService implements Resolve<IRelationshipConstraint | null> {
  constructor(protected service: RelationshipConstraintService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRelationshipConstraint | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((relationshipConstraint: HttpResponse<IRelationshipConstraint>) => {
          if (relationshipConstraint.body) {
            return of(relationshipConstraint.body);
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
