import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramStageInstance } from '../program-stage-instance.model';
import { ProgramStageInstanceService } from '../service/program-stage-instance.service';

@Injectable({ providedIn: 'root' })
export class ProgramStageInstanceRoutingResolveService implements Resolve<IProgramStageInstance | null> {
  constructor(protected service: ProgramStageInstanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramStageInstance | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programStageInstance: HttpResponse<IProgramStageInstance>) => {
          if (programStageInstance.body) {
            return of(programStageInstance.body);
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
