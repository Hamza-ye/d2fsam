import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramTempOwnershipAuditComponent } from '../list/program-temp-ownership-audit.component';
import { ProgramTempOwnershipAuditDetailComponent } from '../detail/program-temp-ownership-audit-detail.component';
import { ProgramTempOwnershipAuditUpdateComponent } from '../update/program-temp-ownership-audit-update.component';
import { ProgramTempOwnershipAuditRoutingResolveService } from './program-temp-ownership-audit-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programTempOwnershipAuditRoute: Routes = [
  {
    path: '',
    component: ProgramTempOwnershipAuditComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramTempOwnershipAuditDetailComponent,
    resolve: {
      programTempOwnershipAudit: ProgramTempOwnershipAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramTempOwnershipAuditUpdateComponent,
    resolve: {
      programTempOwnershipAudit: ProgramTempOwnershipAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramTempOwnershipAuditUpdateComponent,
    resolve: {
      programTempOwnershipAudit: ProgramTempOwnershipAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programTempOwnershipAuditRoute)],
  exports: [RouterModule],
})
export class ProgramTempOwnershipAuditRoutingModule {}
