import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserGroupComponent } from '../list/user-group.component';
import { UserGroupDetailComponent } from '../detail/user-group-detail.component';
import { UserGroupUpdateComponent } from '../update/user-group-update.component';
import { UserGroupRoutingResolveService } from './user-group-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userGroupRoute: Routes = [
  {
    path: '',
    component: UserGroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserGroupDetailComponent,
    resolve: {
      userGroup: UserGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserGroupUpdateComponent,
    resolve: {
      userGroup: UserGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserGroupUpdateComponent,
    resolve: {
      userGroup: UserGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userGroupRoute)],
  exports: [RouterModule],
})
export class UserGroupRoutingModule {}
