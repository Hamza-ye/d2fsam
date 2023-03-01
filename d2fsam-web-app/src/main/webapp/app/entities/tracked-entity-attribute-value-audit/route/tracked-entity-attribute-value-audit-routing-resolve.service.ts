import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';
import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';

@Injectable({ providedIn: 'root' })
export class TrackedEntityAttributeValueAuditRoutingResolveService implements Resolve<ITrackedEntityAttributeValueAudit | null> {
  constructor(protected service: TrackedEntityAttributeValueAuditService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITrackedEntityAttributeValueAudit | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((trackedEntityAttributeValueAudit: HttpResponse<ITrackedEntityAttributeValueAudit>) => {
          if (trackedEntityAttributeValueAudit.body) {
            return of(trackedEntityAttributeValueAudit.body);
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
