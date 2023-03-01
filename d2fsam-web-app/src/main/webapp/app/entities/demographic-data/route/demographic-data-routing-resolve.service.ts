import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDemographicData } from '../demographic-data.model';
import { DemographicDataService } from '../service/demographic-data.service';

@Injectable({ providedIn: 'root' })
export class DemographicDataRoutingResolveService implements Resolve<IDemographicData | null> {
  constructor(protected service: DemographicDataService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDemographicData | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((demographicData: HttpResponse<IDemographicData>) => {
          if (demographicData.body) {
            return of(demographicData.body);
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
