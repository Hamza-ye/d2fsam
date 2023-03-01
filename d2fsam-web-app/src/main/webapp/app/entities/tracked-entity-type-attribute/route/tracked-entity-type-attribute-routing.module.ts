import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityTypeAttributeComponent } from '../list/tracked-entity-type-attribute.component';
import { TrackedEntityTypeAttributeDetailComponent } from '../detail/tracked-entity-type-attribute-detail.component';
import { TrackedEntityTypeAttributeUpdateComponent } from '../update/tracked-entity-type-attribute-update.component';
import { TrackedEntityTypeAttributeRoutingResolveService } from './tracked-entity-type-attribute-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityTypeAttributeRoute: Routes = [
  {
    path: '',
    component: TrackedEntityTypeAttributeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityTypeAttributeDetailComponent,
    resolve: {
      trackedEntityTypeAttribute: TrackedEntityTypeAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityTypeAttributeUpdateComponent,
    resolve: {
      trackedEntityTypeAttribute: TrackedEntityTypeAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityTypeAttributeUpdateComponent,
    resolve: {
      trackedEntityTypeAttribute: TrackedEntityTypeAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityTypeAttributeRoute)],
  exports: [RouterModule],
})
export class TrackedEntityTypeAttributeRoutingModule {}
