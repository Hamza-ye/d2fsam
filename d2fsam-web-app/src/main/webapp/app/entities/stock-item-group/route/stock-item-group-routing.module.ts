import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StockItemGroupComponent } from '../list/stock-item-group.component';
import { StockItemGroupDetailComponent } from '../detail/stock-item-group-detail.component';
import { StockItemGroupUpdateComponent } from '../update/stock-item-group-update.component';
import { StockItemGroupRoutingResolveService } from './stock-item-group-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const stockItemGroupRoute: Routes = [
  {
    path: '',
    component: StockItemGroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StockItemGroupDetailComponent,
    resolve: {
      stockItemGroup: StockItemGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StockItemGroupUpdateComponent,
    resolve: {
      stockItemGroup: StockItemGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StockItemGroupUpdateComponent,
    resolve: {
      stockItemGroup: StockItemGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(stockItemGroupRoute)],
  exports: [RouterModule],
})
export class StockItemGroupRoutingModule {}
