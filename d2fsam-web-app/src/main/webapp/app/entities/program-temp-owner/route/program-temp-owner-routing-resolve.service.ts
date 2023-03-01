import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramTempOwner } from '../program-temp-owner.model';
import { ProgramTempOwnerService } from '../service/program-temp-owner.service';

@Injectable({ providedIn: 'root' })
export class ProgramTempOwnerRoutingResolveService implements Resolve<IProgramTempOwner | null> {
  constructor(protected service: ProgramTempOwnerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramTempOwner | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programTempOwner: HttpResponse<IProgramTempOwner>) => {
          if (programTempOwner.body) {
            return of(programTempOwner.body);
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
