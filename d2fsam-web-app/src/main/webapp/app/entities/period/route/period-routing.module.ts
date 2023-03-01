import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PeriodComponent } from '../list/period.component';
import { PeriodDetailComponent } from '../detail/period-detail.component';
import { PeriodUpdateComponent } from '../update/period-update.component';
import { PeriodRoutingResolveService } from './period-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const periodRoute: Routes = [
  {
    path: '',
    component: PeriodComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PeriodDetailComponent,
    resolve: {
      period: PeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PeriodUpdateComponent,
    resolve: {
      period: PeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PeriodUpdateComponent,
    resolve: {
      period: PeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(periodRoute)],
  exports: [RouterModule],
})
export class PeriodRoutingModule {}
