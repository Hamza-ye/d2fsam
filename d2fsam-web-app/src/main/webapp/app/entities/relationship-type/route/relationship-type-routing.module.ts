import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RelationshipTypeComponent } from '../list/relationship-type.component';
import { RelationshipTypeDetailComponent } from '../detail/relationship-type-detail.component';
import { RelationshipTypeUpdateComponent } from '../update/relationship-type-update.component';
import { RelationshipTypeRoutingResolveService } from './relationship-type-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const relationshipTypeRoute: Routes = [
  {
    path: '',
    component: RelationshipTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RelationshipTypeDetailComponent,
    resolve: {
      relationshipType: RelationshipTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RelationshipTypeUpdateComponent,
    resolve: {
      relationshipType: RelationshipTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RelationshipTypeUpdateComponent,
    resolve: {
      relationshipType: RelationshipTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(relationshipTypeRoute)],
  exports: [RouterModule],
})
export class RelationshipTypeRoutingModule {}
