import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDatastoreEntry } from '../datastore-entry.model';
import { DatastoreEntryService } from '../service/datastore-entry.service';

@Injectable({ providedIn: 'root' })
export class DatastoreEntryRoutingResolveService implements Resolve<IDatastoreEntry | null> {
  constructor(protected service: DatastoreEntryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDatastoreEntry | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((datastoreEntry: HttpResponse<IDatastoreEntry>) => {
          if (datastoreEntry.body) {
            return of(datastoreEntry.body);
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
