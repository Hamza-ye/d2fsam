import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStockItemGroup } from '../stock-item-group.model';
import { StockItemGroupService } from '../service/stock-item-group.service';

@Injectable({ providedIn: 'root' })
export class StockItemGroupRoutingResolveService implements Resolve<IStockItemGroup | null> {
  constructor(protected service: StockItemGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStockItemGroup | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((stockItemGroup: HttpResponse<IStockItemGroup>) => {
          if (stockItemGroup.body) {
            return of(stockItemGroup.body);
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
