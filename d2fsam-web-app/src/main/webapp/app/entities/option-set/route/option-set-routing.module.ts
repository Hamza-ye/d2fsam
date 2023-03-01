import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OptionSetComponent } from '../list/option-set.component';
import { OptionSetDetailComponent } from '../detail/option-set-detail.component';
import { OptionSetUpdateComponent } from '../update/option-set-update.component';
import { OptionSetRoutingResolveService } from './option-set-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const optionSetRoute: Routes = [
  {
    path: '',
    component: OptionSetComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OptionSetDetailComponent,
    resolve: {
      optionSet: OptionSetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OptionSetUpdateComponent,
    resolve: {
      optionSet: OptionSetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OptionSetUpdateComponent,
    resolve: {
      optionSet: OptionSetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(optionSetRoute)],
  exports: [RouterModule],
})
export class OptionSetRoutingModule {}
