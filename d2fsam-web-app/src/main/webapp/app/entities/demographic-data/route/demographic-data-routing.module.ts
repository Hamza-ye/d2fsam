import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DemographicDataComponent } from '../list/demographic-data.component';
import { DemographicDataDetailComponent } from '../detail/demographic-data-detail.component';
import { DemographicDataUpdateComponent } from '../update/demographic-data-update.component';
import { DemographicDataRoutingResolveService } from './demographic-data-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const demographicDataRoute: Routes = [
  {
    path: '',
    component: DemographicDataComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DemographicDataDetailComponent,
    resolve: {
      demographicData: DemographicDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DemographicDataUpdateComponent,
    resolve: {
      demographicData: DemographicDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DemographicDataUpdateComponent,
    resolve: {
      demographicData: DemographicDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(demographicDataRoute)],
  exports: [RouterModule],
})
export class DemographicDataRoutingModule {}
