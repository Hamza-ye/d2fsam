import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISqlView } from '../sql-view.model';
import { SqlViewService } from '../service/sql-view.service';

@Injectable({ providedIn: 'root' })
export class SqlViewRoutingResolveService implements Resolve<ISqlView | null> {
  constructor(protected service: SqlViewService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISqlView | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((sqlView: HttpResponse<ISqlView>) => {
          if (sqlView.body) {
            return of(sqlView.body);
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
