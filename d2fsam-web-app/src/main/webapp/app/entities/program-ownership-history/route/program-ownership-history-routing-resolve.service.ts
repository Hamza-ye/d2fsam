import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramOwnershipHistory } from '../program-ownership-history.model';
import { ProgramOwnershipHistoryService } from '../service/program-ownership-history.service';

@Injectable({ providedIn: 'root' })
export class ProgramOwnershipHistoryRoutingResolveService implements Resolve<IProgramOwnershipHistory | null> {
  constructor(protected service: ProgramOwnershipHistoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramOwnershipHistory | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programOwnershipHistory: HttpResponse<IProgramOwnershipHistory>) => {
          if (programOwnershipHistory.body) {
            return of(programOwnershipHistory.body);
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
