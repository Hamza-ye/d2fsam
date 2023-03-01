import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';
import { TrackedEntityInstanceAuditService } from '../service/tracked-entity-instance-audit.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityInstanceAuditRoutingResolveService implements Resolve<ITrackedEntityInstanceAudit | null> {
  constructor(protected service: TrackedEntityInstanceAuditService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityInstanceAudit | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityInstanceAudit: HttpResponse<ITrackedEntityInstanceAudit>) => {
          if (trackedEntityInstanceAudit.body) {
            return of(trackedEntityInstanceAudit.body);
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
