import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityAttributeValueComponent } from '../list/tracked-entity-attribute-value.component';
import { TrackedEntityAttributeValueDetailComponent } from '../detail/tracked-entity-attribute-value-detail.component';
import { TrackedEntityAttributeValueUpdateComponent } from '../update/tracked-entity-attribute-value-update.component';
import { TrackedEntityAttributeValueRoutingResolveService } from './tracked-entity-attribute-value-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityAttributeValueRoute: Routes = [
  {
    path: '',
    component: TrackedEntityAttributeValueComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityAttributeValueDetailComponent,
    resolve: {
      trackedEntityAttributeValue: TrackedEntityAttributeValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityAttributeValueUpdateComponent,
    resolve: {
      trackedEntityAttributeValue: TrackedEntityAttributeValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityAttributeValueUpdateComponent,
    resolve: {
      trackedEntityAttributeValue: TrackedEntityAttributeValueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityAttributeValueRoute)],
  exports: [RouterModule],
})
export class TrackedEntityAttributeValueRoutingModule {}
