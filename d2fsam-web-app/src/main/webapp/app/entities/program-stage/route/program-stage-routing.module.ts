import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramStageComponent } from '../list/program-stage.component';
import { ProgramStageDetailComponent } from '../detail/program-stage-detail.component';
import { ProgramStageUpdateComponent } from '../update/program-stage-update.component';
import { ProgramStageRoutingResolveService } from './program-stage-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programStageRoute: Routes = [
  {
    path: '',
    component: ProgramStageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramStageDetailComponent,
    resolve: {
      programStage: ProgramStageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramStageUpdateComponent,
    resolve: {
      programStage: ProgramStageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramStageUpdateComponent,
    resolve: {
      programStage: ProgramStageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programStageRoute)],
  exports: [RouterModule],
})
export class ProgramStageRoutingModule {}
