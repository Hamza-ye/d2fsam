import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramOwnershipHistoryComponent } from '../list/program-ownership-history.component';
import { ProgramOwnershipHistoryDetailComponent } from '../detail/program-ownership-history-detail.component';
import { ProgramOwnershipHistoryUpdateComponent } from '../update/program-ownership-history-update.component';
import { ProgramOwnershipHistoryRoutingResolveService } from './program-ownership-history-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programOwnershipHistoryRoute: Routes = [
  {
    path: '',
    component: ProgramOwnershipHistoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramOwnershipHistoryDetailComponent,
    resolve: {
      programOwnershipHistory: ProgramOwnershipHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramOwnershipHistoryUpdateComponent,
    resolve: {
      programOwnershipHistory: ProgramOwnershipHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramOwnershipHistoryUpdateComponent,
    resolve: {
      programOwnershipHistory: ProgramOwnershipHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programOwnershipHistoryRoute)],
  exports: [RouterModule],
})
export class ProgramOwnershipHistoryRoutingModule {}
