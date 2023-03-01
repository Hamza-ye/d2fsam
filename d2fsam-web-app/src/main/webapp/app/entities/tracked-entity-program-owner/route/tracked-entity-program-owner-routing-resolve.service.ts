import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';
import { TrackedEntityProgramOwnerService } from '../service/tracked-entity-program-owner.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityProgramOwnerRoutingResolveService implements Resolve<ITrackedEntityProgramOwner | null> {
  constructor(protected service: TrackedEntityProgramOwnerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityProgramOwner | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityProgramOwner: HttpResponse<ITrackedEntityProgramOwner>) => {
          if (trackedEntityProgramOwner.body) {
            return of(trackedEntityProgramOwner.body);
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
