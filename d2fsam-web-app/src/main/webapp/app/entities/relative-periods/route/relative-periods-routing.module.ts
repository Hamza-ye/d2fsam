import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RelativePeriodsComponent } from '../list/relative-periods.component';
import { RelativePeriodsDetailComponent } from '../detail/relative-periods-detail.component';
import { RelativePeriodsUpdateComponent } from '../update/relative-periods-update.component';
import { RelativePeriodsRoutingResolveService } from './relative-periods-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const relativePeriodsRoute: Routes = [
  {
    path: '',
    component: RelativePeriodsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RelativePeriodsDetailComponent,
    resolve: {
      relativePeriods: RelativePeriodsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RelativePeriodsUpdateComponent,
    resolve: {
      relativePeriods: RelativePeriodsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RelativePeriodsUpdateComponent,
    resolve: {
      relativePeriods: RelativePeriodsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(relativePeriodsRoute)],
  exports: [RouterModule],
})
export class RelativePeriodsRoutingModule {}
