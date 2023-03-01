import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TrackedEntityAttributeValueAuditComponent } from '../list/tracked-entity-attribute-value-audit.component';
import { TrackedEntityAttributeValueAuditDetailComponent } from '../detail/tracked-entity-attribute-value-audit-detail.component';
import { TrackedEntityAttributeValueAuditUpdateComponent } from '../update/tracked-entity-attribute-value-audit-update.component';
import { TrackedEntityAttributeValueAuditRoutingResolveService } from './tracked-entity-attribute-value-audit-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const trackedEntityAttributeValueAuditRoute: Routes = [
  {
    path: '',
    component: TrackedEntityAttributeValueAuditComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrackedEntityAttributeValueAuditDetailComponent,
    resolve: {
      trackedEntityAttributeValueAudit: TrackedEntityAttributeValueAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrackedEntityAttributeValueAuditUpdateComponent,
    resolve: {
      trackedEntityAttributeValueAudit: TrackedEntityAttributeValueAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrackedEntityAttributeValueAuditUpdateComponent,
    resolve: {
      trackedEntityAttributeValueAudit: TrackedEntityAttributeValueAuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(trackedEntityAttributeValueAuditRoute)],
  exports: [RouterModule],
})
export class TrackedEntityAttributeValueAuditRoutingModule {}
