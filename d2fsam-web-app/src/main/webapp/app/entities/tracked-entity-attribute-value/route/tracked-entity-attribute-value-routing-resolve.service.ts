import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';
import { TrackedEntityAttributeValueService } from '../service/tracked-entity-attribute-value.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityAttributeValueRoutingResolveService implements Resolve<ITrackedEntityAttributeValue | null> {
  constructor(protected service: TrackedEntityAttributeValueService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityAttributeValue | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityAttributeValue: HttpResponse<ITrackedEntityAttributeValue>) => {
          if (trackedEntityAttributeValue.body) {
            return of(trackedEntityAttributeValue.body);
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
