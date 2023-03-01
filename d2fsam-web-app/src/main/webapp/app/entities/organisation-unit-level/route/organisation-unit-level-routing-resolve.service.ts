import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrganisationUnitLevel } from '../organisation-unit-level.model';
import { OrganisationUnitLevelService } from '../service/organisation-unit-level.service';

@Injectable({ providedIn: 'root' })
export class OrganisationUnitLevelRoutingResolveService implements Resolve<IOrganisationUnitLevel | null> {
  constructor(protected service: OrganisationUnitLevelService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrganisationUnitLevel | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((organisationUnitLevel: HttpResponse<IOrganisationUnitLevel>) => {
          if (organisationUnitLevel.body) {
            return of(organisationUnitLevel.body);
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
