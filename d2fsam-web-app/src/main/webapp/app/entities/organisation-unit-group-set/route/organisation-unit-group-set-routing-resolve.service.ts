import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrganisationUnitGroupSet } from '../organisation-unit-group-set.model';
import { OrganisationUnitGroupSetService } from '../service/organisation-unit-group-set.service';

@Injectable({ providedIn: 'root' })
export class OrganisationUnitGroupSetRoutingResolveService implements Resolve<IOrganisationUnitGroupSet | null> {
  constructor(protected service: OrganisationUnitGroupSetService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrganisationUnitGroupSet | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((organisationUnitGroupSet: HttpResponse<IOrganisationUnitGroupSet>) => {
          if (organisationUnitGroupSet.body) {
            return of(organisationUnitGroupSet.body);
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
