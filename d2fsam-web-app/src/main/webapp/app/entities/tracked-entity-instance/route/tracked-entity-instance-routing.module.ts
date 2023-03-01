import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityInstanceComponent } from '../list/tracked-entity-instance.component';
import { TrackedEntityInstanceDetailComponent } from '../detail/tracked-entity-instance-detail.component';
import { TrackedEntityInstanceUpdateComponent } from '../update/tracked-entity-instance-update.component';
import { TrackedEntityInstanceRoutingResolveService } from './tracked-entity-instance-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityInstanceRoute: Routes = [
  {
    path: '',
    component: TrackedEntityInstanceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityInstanceDetailComponent,
    resolve: {
      trackedEntityInstance: TrackedEntityInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityInstanceUpdateComponent,
    resolve: {
      trackedEntityInstance: TrackedEntityInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityInstanceUpdateComponent,
    resolve: {
      trackedEntityInstance: TrackedEntityInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityInstanceRoute)],
  exports: [RouterModule],
})
export class TrackedEntityInstanceRoutingModule {}
