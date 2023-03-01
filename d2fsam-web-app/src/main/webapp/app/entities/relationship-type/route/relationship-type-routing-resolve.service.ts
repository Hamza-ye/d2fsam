import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRelationshipType } from '../relationship-type.model';
import { RelationshipTypeService } from '../service/relationship-type.service';

@Injectable({ providedIn: 'root' })
export class RelationshipTypeRoutingResolveService implements Resolve<IRelationshipType | null> {
  constructor(protected service: RelationshipTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRelationshipType | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((relationshipType: HttpResponse<IRelationshipType>) => {
          if (relationshipType.body) {
            return of(relationshipType.body);
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
