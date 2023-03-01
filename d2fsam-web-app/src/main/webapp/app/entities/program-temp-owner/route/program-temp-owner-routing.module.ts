import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramTempOwnerComponent } from '../list/program-temp-owner.component';
import { ProgramTempOwnerDetailComponent } from '../detail/program-temp-owner-detail.component';
import { ProgramTempOwnerUpdateComponent } from '../update/program-temp-owner-update.component';
import { ProgramTempOwnerRoutingResolveService } from './program-temp-owner-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programTempOwnerRoute: Routes = [
  {
    path: '',
    component: ProgramTempOwnerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramTempOwnerDetailComponent,
    resolve: {
      programTempOwner: ProgramTempOwnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramTempOwnerUpdateComponent,
    resolve: {
      programTempOwner: ProgramTempOwnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramTempOwnerUpdateComponent,
    resolve: {
      programTempOwner: ProgramTempOwnerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programTempOwnerRoute)],
  exports: [RouterModule],
})
export class ProgramTempOwnerRoutingModule {}
