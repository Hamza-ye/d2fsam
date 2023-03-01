import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserAuthorityGroup } from '../user-authority-group.model';
import { UserAuthorityGroupService } from '../service/user-authority-group.service';

@Injectable({ providedIn: 'root' })
export class UserAuthorityGroupRoutingResolveService implements Resolve<IUserAuthorityGroup | null> {
  constructor(protected service: UserAuthorityGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserAuthorityGroup | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userAuthorityGroup: HttpResponse<IUserAuthorityGroup>) => {
          if (userAuthorityGroup.body) {
            return of(userAuthorityGroup.body);
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
