import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOptionSet } from '../option-set.model';
import { OptionSetService } from '../service/option-set.service';

@Injectable({ providedIn: 'root' })
export class OptionSetRoutingResolveService implements Resolve<IOptionSet | null> {
  constructor(protected service: OptionSetService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOptionSet | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((optionSet: HttpResponse<IOptionSet>) => {
          if (optionSet.body) {
            return of(optionSet.body);
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
