import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRelationshipItem } from '../relationship-item.model';
import { RelationshipItemService } from '../service/relationship-item.service';

@Injectable({ providedIn: 'root' })
export class RelationshipItemRoutingResolveService implements Resolve<IRelationshipItem | null> {
  constructor(protected service: RelationshipItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRelationshipItem | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((relationshipItem: HttpResponse<IRelationshipItem>) => {
          if (relationshipItem.body) {
            return of(relationshipItem.body);
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
