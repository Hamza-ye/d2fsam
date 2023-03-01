import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityInstance } from '../tracked-entity-instance.model';
import { TrackedEntityInstanceService } from '../service/tracked-entity-instance.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceRoutingResolveService implements Resolve<ITrackedEntityInstance | null> {
  constructor(protected service: TrackedEntityInstanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityInstance | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityInstance: HttpResponse<ITrackedEntityInstance>) => {
          if (trackedEntityInstance.body) {
            return of(trackedEntityInstance.body);
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
