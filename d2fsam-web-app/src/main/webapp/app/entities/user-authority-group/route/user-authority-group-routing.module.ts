import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserAuthorityGroupComponent } from '../list/user-authority-group.component';
import { UserAuthorityGroupDetailComponent } from '../detail/user-authority-group-detail.component';
import { UserAuthorityGroupUpdateComponent } from '../update/user-authority-group-update.component';
import { UserAuthorityGroupRoutingResolveService } from './user-authority-group-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userAuthorityGroupRoute: Routes = [
  {
    path: '',
    component: UserAuthorityGroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserAuthorityGroupDetailComponent,
    resolve: {
      userAuthorityGroup: UserAuthorityGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserAuthorityGroupUpdateComponent,
    resolve: {
      userAuthorityGroup: UserAuthorityGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserAuthorityGroupUpdateComponent,
    resolve: {
      userAuthorityGroup: UserAuthorityGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userAuthorityGroupRoute)],
  exports: [RouterModule],
})
export class UserAuthorityGroupRoutingModule {}
