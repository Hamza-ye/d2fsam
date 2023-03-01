import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';
import { ProgramStageInstanceFilterService } from '../service/program-stage-instance-filter.service';

@Injectable({ providedIn: 'root' })
export class ProgramStageInstanceFilterRoutingResolveService implements Resolve<IProgramStageInstanceFilter | null> {
  constructor(protected service: ProgramStageInstanceFilterService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramStageInstanceFilter | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programStageInstanceFilter: HttpResponse<IProgramStageInstanceFilter>) => {
          if (programStageInstanceFilter.body) {
            return of(programStageInstanceFilter.body);
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
