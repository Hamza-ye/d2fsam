import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityType } from '../tracked-entity-type.model';
import { TrackedEntityTypeService } from '../service/tracked-entity-type.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityTypeRoutingResolveService implements Resolve<ITrackedEntityType | null> {
  constructor(protected service: TrackedEntityTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityType | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityType: HttpResponse<ITrackedEntityType>) => {
          if (trackedEntityType.body) {
            return of(trackedEntityType.body);
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
