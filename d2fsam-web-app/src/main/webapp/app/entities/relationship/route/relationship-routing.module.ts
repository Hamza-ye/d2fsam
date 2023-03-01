import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RelationshipComponent } from '../list/relationship.component';
import { RelationshipDetailComponent } from '../detail/relationship-detail.component';
import { RelationshipUpdateComponent } from '../update/relationship-update.component';
import { RelationshipRoutingResolveService } from './relationship-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const relationshipRoute: Routes = [
  {
    path: '',
    component: RelationshipComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RelationshipDetailComponent,
    resolve: {
      relationship: RelationshipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RelationshipUpdateComponent,
    resolve: {
      relationship: RelationshipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RelationshipUpdateComponent,
    resolve: {
      relationship: RelationshipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(relationshipRoute)],
  exports: [RouterModule],
})
export class RelationshipRoutingModule {}
