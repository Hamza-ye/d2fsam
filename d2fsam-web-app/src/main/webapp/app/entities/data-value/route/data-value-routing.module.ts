import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DataValueComponent } from '../list/data-value.component';
import { DataValueDetailComponent } from '../detail/data-value-detail.component';
import { DataValueUpdateComponent } from '../update/data-value-update.component';
import { DataValueRoutingResolveService } from './data-value-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const dataValueRoute: Routes = [
  {
    path: '',
    component: DataValueComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DataValueDetailComponent,
    resolve: {
      dataValue: DataValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DataValueUpdateComponent,
    resolve: {
      dataValue: DataValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DataValueUpdateComponent,
    resolve: {
      dataValue: DataValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dataValueRoute)],
  exports: [RouterModule],
})
export class DataValueRoutingModule {}
