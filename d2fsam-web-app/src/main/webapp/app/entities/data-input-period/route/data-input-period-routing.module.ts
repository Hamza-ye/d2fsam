import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DataInputPeriodComponent } from '../list/data-input-period.component';
import { DataInputPeriodDetailComponent } from '../detail/data-input-period-detail.component';
import { DataInputPeriodUpdateComponent } from '../update/data-input-period-update.component';
import { DataInputPeriodRoutingResolveService } from './data-input-period-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const dataInputPeriodRoute: Routes = [
  {
    path: '',
    component: DataInputPeriodComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DataInputPeriodDetailComponent,
    resolve: {
      dataInputPeriod: DataInputPeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DataInputPeriodUpdateComponent,
    resolve: {
      dataInputPeriod: DataInputPeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DataInputPeriodUpdateComponent,
    resolve: {
      dataInputPeriod: DataInputPeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dataInputPeriodRoute)],
  exports: [RouterModule],
})
export class DataInputPeriodRoutingModule {}
