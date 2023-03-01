import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISystemSetting } from '../system-setting.model';
import { SystemSettingService } from '../service/system-setting.service';

@Injectable({ providedIn: 'root' })
export class SystemSettingRoutingResolveService implements Resolve<ISystemSetting | null> {
  constructor(protected service: SystemSettingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISystemSetting | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((systemSetting: HttpResponse<ISystemSetting>) => {
          if (systemSetting.body) {
            return of(systemSetting.body);
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
