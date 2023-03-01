import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityInstanceFilterComponent } from '../list/tracked-entity-instance-filter.component';
import { TrackedEntityInstanceFilterDetailComponent } from '../detail/tracked-entity-instance-filter-detail.component';
import { TrackedEntityInstanceFilterUpdateComponent } from '../update/tracked-entity-instance-filter-update.component';
import { TrackedEntityInstanceFilterRoutingResolveService } from './tracked-entity-instance-filter-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityInstanceFilterRoute: Routes = [
  {
    path: '',
    component: TrackedEntityInstanceFilterComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityInstanceFilterDetailComponent,
    resolve: {
      trackedEntityInstanceFilter: TrackedEntityInstanceFilterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityInstanceFilterUpdateComponent,
    resolve: {
      trackedEntityInstanceFilter: TrackedEntityInstanceFilterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityInstanceFilterUpdateComponent,
    resolve: {
      trackedEntityInstanceFilter: TrackedEntityInstanceFilterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityInstanceFilterRoute)],
  exports: [RouterModule],
})
export class TrackedEntityInstanceFilterRoutingModule {}
