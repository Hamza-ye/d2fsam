import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import { TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceFilterRoutingResolveService implements Resolve<ITrackedEntityInstanceFilter | null> {
  constructor(protected service: TrackedEntityInstanceFilterService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityInstanceFilter | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityInstanceFilter: HttpResponse<ITrackedEntityInstanceFilter>) => {
          if (trackedEntityInstanceFilter.body) {
            return of(trackedEntityInstanceFilter.body);
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
