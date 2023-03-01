import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';
import { TrackedEntityTypeAttributeService } from '../service/tracked-entity-type-attribute.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityTypeAttributeRoutingResolveService implements Resolve<ITrackedEntityTypeAttribute | null> {
  constructor(protected service: TrackedEntityTypeAttributeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityTypeAttribute | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityTypeAttribute: HttpResponse<ITrackedEntityTypeAttribute>) => {
          if (trackedEntityTypeAttribute.body) {
            return of(trackedEntityTypeAttribute.body);
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
