import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityInstanceAuditComponent } from '../list/tracked-entity-instance-audit.component';
import { TrackedEntityInstanceAuditDetailComponent } from '../detail/tracked-entity-instance-audit-detail.component';
import { TrackedEntityInstanceAuditUpdateComponent } from '../update/tracked-entity-instance-audit-update.component';
import { TrackedEntityInstanceAuditRoutingResolveService } from './tracked-entity-instance-audit-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityInstanceAuditRoute: Routes = [
  {
    path: '',
    component: TrackedEntityInstanceAuditComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityInstanceAuditDetailComponent,
    resolve: {
      trackedEntityInstanceAudit: TrackedEntityInstanceAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityInstanceAuditUpdateComponent,
    resolve: {
      trackedEntityInstanceAudit: TrackedEntityInstanceAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityInstanceAuditUpdateComponent,
    resolve: {
      trackedEntityInstanceAudit: TrackedEntityInstanceAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityInstanceAuditRoute)],
  exports: [RouterModule],
})
export class TrackedEntityInstanceAuditRoutingModule {}
