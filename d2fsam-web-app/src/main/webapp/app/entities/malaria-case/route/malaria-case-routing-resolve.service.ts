import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMalariaCase } from '../malaria-case.model';
import { MalariaCaseService } from '../service/malaria-case.service';

@Injectable({ providedIn: 'root' })
export class MalariaCaseRoutingResolveService implements Resolve<IMalariaCase | null> {
  constructor(protected service: MalariaCaseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMalariaCase | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((malariaCase: HttpResponse<IMalariaCase>) => {
          if (malariaCase.body) {
            return of(malariaCase.body);
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
