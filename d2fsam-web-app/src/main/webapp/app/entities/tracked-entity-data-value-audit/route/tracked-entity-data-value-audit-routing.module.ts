import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityDataValueAuditComponent } from '../list/tracked-entity-data-value-audit.component';
import { TrackedEntityDataValueAuditDetailComponent } from '../detail/tracked-entity-data-value-audit-detail.component';
import { TrackedEntityDataValueAuditUpdateComponent } from '../update/tracked-entity-data-value-audit-update.component';
import { TrackedEntityDataValueAuditRoutingResolveService } from './tracked-entity-data-value-audit-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityDataValueAuditRoute: Routes = [
  {
    path: '',
    component: TrackedEntityDataValueAuditComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityDataValueAuditDetailComponent,
    resolve: {
      trackedEntityDataValueAudit: TrackedEntityDataValueAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityDataValueAuditUpdateComponent,
    resolve: {
      trackedEntityDataValueAudit: TrackedEntityDataValueAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityDataValueAuditUpdateComponent,
    resolve: {
      trackedEntityDataValueAudit: TrackedEntityDataValueAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityDataValueAuditRoute)],
  exports: [RouterModule],
})
export class TrackedEntityDataValueAuditRoutingModule {}
