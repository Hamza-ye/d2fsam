import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';
import { ProgramTrackedEntityAttributeService } from '../service/program-tracked-entity-attribute.service';

@Injectable({ providedIn: 'root' })
export class ProgramTrackedEntityAttributeRoutingResolveService implements Resolve<IProgramTrackedEntityAttribute | null> {
  constructor(protected service: ProgramTrackedEntityAttributeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramTrackedEntityAttribute | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programTrackedEntityAttribute: HttpResponse<IProgramTrackedEntityAttribute>) => {
          if (programTrackedEntityAttribute.body) {
            return of(programTrackedEntityAttribute.body);
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
