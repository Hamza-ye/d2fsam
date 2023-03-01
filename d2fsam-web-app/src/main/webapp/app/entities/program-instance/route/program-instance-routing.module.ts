import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramInstanceComponent } from '../list/program-instance.component';
import { ProgramInstanceDetailComponent } from '../detail/program-instance-detail.component';
import { ProgramInstanceUpdateComponent } from '../update/program-instance-update.component';
import { ProgramInstanceRoutingResolveService } from './program-instance-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programInstanceRoute: Routes = [
  {
    path: '',
    component: ProgramInstanceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramInstanceDetailComponent,
    resolve: {
      programInstance: ProgramInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramInstanceUpdateComponent,
    resolve: {
      programInstance: ProgramInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramInstanceUpdateComponent,
    resolve: {
      programInstance: ProgramInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programInstanceRoute)],
  exports: [RouterModule],
})
export class ProgramInstanceRoutingModule {}
