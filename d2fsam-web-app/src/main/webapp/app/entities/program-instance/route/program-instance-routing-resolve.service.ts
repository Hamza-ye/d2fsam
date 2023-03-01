import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramInstance } from '../program-instance.model';
import { ProgramInstanceService } from '../service/program-instance.service';

@Injectable({ providedIn: 'root' })
export class ProgramInstanceRoutingResolveService implements Resolve<IProgramInstance | null> {
  constructor(protected service: ProgramInstanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramInstance | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programInstance: HttpResponse<IProgramInstance>) => {
          if (programInstance.body) {
            return of(programInstance.body);
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
