import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityProgramOwnerComponent } from '../list/tracked-entity-program-owner.component';
import { TrackedEntityProgramOwnerDetailComponent } from '../detail/tracked-entity-program-owner-detail.component';
import { TrackedEntityProgramOwnerUpdateComponent } from '../update/tracked-entity-program-owner-update.component';
import { TrackedEntityProgramOwnerRoutingResolveService } from './tracked-entity-program-owner-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityProgramOwnerRoute: Routes = [
  {
    path: '',
    component: TrackedEntityProgramOwnerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityProgramOwnerDetailComponent,
    resolve: {
      trackedEntityProgramOwner: TrackedEntityProgramOwnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityProgramOwnerUpdateComponent,
    resolve: {
      trackedEntityProgramOwner: TrackedEntityProgramOwnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityProgramOwnerUpdateComponent,
    resolve: {
      trackedEntityProgramOwner: TrackedEntityProgramOwnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityProgramOwnerRoute)],
  exports: [RouterModule],
})
export class TrackedEntityProgramOwnerRoutingModule {}
