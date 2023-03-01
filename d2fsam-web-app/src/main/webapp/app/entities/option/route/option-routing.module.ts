import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OptionComponent } from '../list/option.component';
import { OptionDetailComponent } from '../detail/option-detail.component';
import { OptionUpdateComponent } from '../update/option-update.component';
import { OptionRoutingResolveService } from './option-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const optionRoute: Routes = [
  {
    path: '',
    component: OptionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OptionDetailComponent,
    resolve: {
      option: OptionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OptionUpdateComponent,
    resolve: {
      option: OptionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OptionUpdateComponent,
    resolve: {
      option: OptionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(optionRoute)],
  exports: [RouterModule],
})
export class OptionRoutingModule {}
