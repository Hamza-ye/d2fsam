import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramStageInstanceComponent } from '../list/program-stage-instance.component';
import { ProgramStageInstanceDetailComponent } from '../detail/program-stage-instance-detail.component';
import { ProgramStageInstanceUpdateComponent } from '../update/program-stage-instance-update.component';
import { ProgramStageInstanceRoutingResolveService } from './program-stage-instance-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programStageInstanceRoute: Routes = [
  {
    path: '',
    component: ProgramStageInstanceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramStageInstanceDetailComponent,
    resolve: {
      programStageInstance: ProgramStageInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramStageInstanceUpdateComponent,
    resolve: {
      programStageInstance: ProgramStageInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramStageInstanceUpdateComponent,
    resolve: {
      programStageInstance: ProgramStageInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programStageInstanceRoute)],
  exports: [RouterModule],
})
export class ProgramStageInstanceRoutingModule {}
