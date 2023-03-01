import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityTypeComponent } from '../list/tracked-entity-type.component';
import { TrackedEntityTypeDetailComponent } from '../detail/tracked-entity-type-detail.component';
import { TrackedEntityTypeUpdateComponent } from '../update/tracked-entity-type-update.component';
import { TrackedEntityTypeRoutingResolveService } from './tracked-entity-type-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityTypeRoute: Routes = [
  {
    path: '',
    component: TrackedEntityTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityTypeDetailComponent,
    resolve: {
      trackedEntityType: TrackedEntityTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityTypeUpdateComponent,
    resolve: {
      trackedEntityType: TrackedEntityTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityTypeUpdateComponent,
    resolve: {
      trackedEntityType: TrackedEntityTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityTypeRoute)],
  exports: [RouterModule],
})
export class TrackedEntityTypeRoutingModule {}
