import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMetadataVersion } from '../metadata-version.model';
import { MetadataVersionService } from '../service/metadata-version.service';

@Injectable({ providedIn: 'root' })
export class MetadataVersionRoutingResolveService implements Resolve<IMetadataVersion | null> {
  constructor(protected service: MetadataVersionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMetadataVersion | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((metadataVersion: HttpResponse<IMetadataVersion>) => {
          if (metadataVersion.body) {
            return of(metadataVersion.body);
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
