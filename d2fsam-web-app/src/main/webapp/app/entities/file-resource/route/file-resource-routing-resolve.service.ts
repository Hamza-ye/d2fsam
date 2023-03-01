import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFileResource } from '../file-resource.model';
import { FileResourceService } from '../service/file-resource.service';

@Injectable({ providedIn: 'root' })
export class FileResourceRoutingResolveService implements Resolve<IFileResource | null> {
  constructor(protected service: FileResourceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFileResource | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((fileResource: HttpResponse<IFileResource>) => {
          if (fileResource.body) {
            return of(fileResource.body);
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
