import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramStageInstanceFilterComponent } from '../list/program-stage-instance-filter.component';
import { ProgramStageInstanceFilterDetailComponent } from '../detail/program-stage-instance-filter-detail.component';
import { ProgramStageInstanceFilterUpdateComponent } from '../update/program-stage-instance-filter-update.component';
import { ProgramStageInstanceFilterRoutingResolveService } from './program-stage-instance-filter-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programStageInstanceFilterRoute: Routes = [
  {
    path: '',
    component: ProgramStageInstanceFilterComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramStageInstanceFilterDetailComponent,
    resolve: {
      programStageInstanceFilter: ProgramStageInstanceFilterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramStageInstanceFilterUpdateComponent,
    resolve: {
      programStageInstanceFilter: ProgramStageInstanceFilterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramStageInstanceFilterUpdateComponent,
    resolve: {
      programStageInstanceFilter: ProgramStageInstanceFilterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programStageInstanceFilterRoute)],
  exports: [RouterModule],
})
export class ProgramStageInstanceFilterRoutingModule {}
