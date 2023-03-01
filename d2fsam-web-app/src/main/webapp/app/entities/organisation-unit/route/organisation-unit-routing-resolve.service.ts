import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrganisationUnit } from '../organisation-unit.model';
import { OrganisationUnitService } from '../service/organisation-unit.service';

@Injectable({ providedIn: 'root' })
export class OrganisationUnitRoutingResolveService implements Resolve<IOrganisationUnit | null> {
  constructor(protected service: OrganisationUnitService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrganisationUnit | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((organisationUnit: HttpResponse<IOrganisationUnit>) => {
          if (organisationUnit.body) {
            return of(organisationUnit.body);
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
