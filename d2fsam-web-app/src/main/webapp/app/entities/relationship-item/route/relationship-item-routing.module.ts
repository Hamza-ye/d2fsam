import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RelationshipItemComponent } from '../list/relationship-item.component';
import { RelationshipItemDetailComponent } from '../detail/relationship-item-detail.component';
import { RelationshipItemUpdateComponent } from '../update/relationship-item-update.component';
import { RelationshipItemRoutingResolveService } from './relationship-item-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const relationshipItemRoute: Routes = [
  {
    path: '',
    component: RelationshipItemComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RelationshipItemDetailComponent,
    resolve: {
      relationshipItem: RelationshipItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RelationshipItemUpdateComponent,
    resolve: {
      relationshipItem: RelationshipItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RelationshipItemUpdateComponent,
    resolve: {
      relationshipItem: RelationshipItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(relationshipItemRoute)],
  exports: [RouterModule],
})
export class RelationshipItemRoutingModule {}
