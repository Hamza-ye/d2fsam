import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';
import { TrackedEntityDataValueAuditService } from '../service/tracked-entity-data-value-audit.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityDataValueAuditRoutingResolveService implements Resolve<ITrackedEntityDataValueAudit | null> {
  constructor(protected service: TrackedEntityDataValueAuditService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityDataValueAudit | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityDataValueAudit: HttpResponse<ITrackedEntityDataValueAudit>) => {
          if (trackedEntityDataValueAudit.body) {
            return of(trackedEntityDataValueAudit.body);
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
