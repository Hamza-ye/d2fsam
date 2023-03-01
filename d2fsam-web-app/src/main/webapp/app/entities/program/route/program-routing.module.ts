import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramComponent } from '../list/program.component';
import { ProgramDetailComponent } from '../detail/program-detail.component';
import { ProgramUpdateComponent } from '../update/program-update.component';
import { ProgramRoutingResolveService } from './program-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programRoute: Routes = [
  {
    path: '',
    component: ProgramComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramDetailComponent,
    resolve: {
      program: ProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramUpdateComponent,
    resolve: {
      program: ProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramUpdateComponent,
    resolve: {
      program: ProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programRoute)],
  exports: [RouterModule],
})
export class ProgramRoutingModule {}
