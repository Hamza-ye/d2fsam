import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserGroup } from '../user-group.model';
import { UserGroupService } from '../service/user-group.service';

@Injectable({ providedIn: 'root' })
export class UserGroupRoutingResolveService implements Resolve<IUserGroup | null> {
  constructor(protected service: UserGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserGroup | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userGroup: HttpResponse<IUserGroup>) => {
          if (userGroup.body) {
            return of(userGroup.body);
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
