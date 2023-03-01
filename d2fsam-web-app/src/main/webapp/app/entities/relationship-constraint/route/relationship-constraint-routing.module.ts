import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RelationshipConstraintComponent } from '../list/relationship-constraint.component';
import { RelationshipConstraintDetailComponent } from '../detail/relationship-constraint-detail.component';
import { RelationshipConstraintUpdateComponent } from '../update/relationship-constraint-update.component';
import { RelationshipConstraintRoutingResolveService } from './relationship-constraint-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const relationshipConstraintRoute: Routes = [
  {
    path: '',
    component: RelationshipConstraintComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RelationshipConstraintDetailComponent,
    resolve: {
      relationshipConstraint: RelationshipConstraintRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RelationshipConstraintUpdateComponent,
    resolve: {
      relationshipConstraint: RelationshipConstraintRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RelationshipConstraintUpdateComponent,
    resolve: {
      relationshipConstraint: RelationshipConstraintRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(relationshipConstraintRoute)],
  exports: [RouterModule],
})
export class RelationshipConstraintRoutingModule {}
