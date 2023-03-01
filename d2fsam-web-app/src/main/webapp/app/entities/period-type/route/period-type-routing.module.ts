import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PeriodTypeComponent } from '../list/period-type.component';
import { PeriodTypeDetailComponent } from '../detail/period-type-detail.component';
import { PeriodTypeUpdateComponent } from '../update/period-type-update.component';
import { PeriodTypeRoutingResolveService } from './period-type-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const periodTypeRoute: Routes = [
  {
    path: '',
    component: PeriodTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PeriodTypeDetailComponent,
    resolve: {
      periodType: PeriodTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PeriodTypeUpdateComponent,
    resolve: {
      periodType: PeriodTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PeriodTypeUpdateComponent,
    resolve: {
      periodType: PeriodTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(periodTypeRoute)],
  exports: [RouterModule],
})
export class PeriodTypeRoutingModule {}
