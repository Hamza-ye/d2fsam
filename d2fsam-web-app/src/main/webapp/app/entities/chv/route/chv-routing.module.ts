import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ChvComponent } from '../list/chv.component';
import { ChvDetailComponent } from '../detail/chv-detail.component';
import { ChvUpdateComponent } from '../update/chv-update.component';
import { ChvRoutingResolveService } from './chv-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const chvRoute: Routes = [
  {
    path: '',
    component: ChvComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ChvDetailComponent,
    resolve: {
      chv: ChvRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ChvUpdateComponent,
    resolve: {
      chv: ChvRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ChvUpdateComponent,
    resolve: {
      chv: ChvRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(chvRoute)],
  exports: [RouterModule],
})
export class ChvRoutingModule {}
