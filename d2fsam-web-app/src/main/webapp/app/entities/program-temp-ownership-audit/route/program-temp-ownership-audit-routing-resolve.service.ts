import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';
import { ProgramTempOwnershipAuditService } from '../service/program-temp-ownership-audit.service';

@Injectable({ providedIn: 'root' })
export class ProgramTempOwnershipAuditRoutingResolveService implements Resolve<IProgramTempOwnershipAudit | null> {
  constructor(protected service: ProgramTempOwnershipAuditService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramTempOwnershipAudit | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programTempOwnershipAudit: HttpResponse<IProgramTempOwnershipAudit>) => {
          if (programTempOwnershipAudit.body) {
            return of(programTempOwnershipAudit.body);
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
