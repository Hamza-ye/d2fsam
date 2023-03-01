import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AssignmentComponent } from '../list/assignment.component';
import { AssignmentDetailComponent } from '../detail/assignment-detail.component';
import { AssignmentUpdateComponent } from '../update/assignment-update.component';
import { AssignmentRoutingResolveService } from './assignment-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const assignmentRoute: Routes = [
  {
    path: '',
    component: AssignmentComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AssignmentDetailComponent,
    resolve: {
      assignment: AssignmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AssignmentUpdateComponent,
    resolve: {
      assignment: AssignmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AssignmentUpdateComponent,
    resolve: {
      assignment: AssignmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(assignmentRoute)],
  exports: [RouterModule],
})
export class AssignmentRoutingModule {}
