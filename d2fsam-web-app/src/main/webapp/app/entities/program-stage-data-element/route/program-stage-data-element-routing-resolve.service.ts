import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProgramStageDataElement } from '../program-stage-data-element.model';
import { ProgramStageDataElementService } from '../service/program-stage-data-element.service';

@Injectable({ providedIn: 'root' })
export class ProgramStageDataElementRoutingResolveService implements Resolve<IProgramStageDataElement | null> {
  constructor(protected service: ProgramStageDataElementService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProgramStageDataElement | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((programStageDataElement: HttpResponse<IProgramStageDataElement>) => {
          if (programStageDataElement.body) {
            return of(programStageDataElement.body);
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
