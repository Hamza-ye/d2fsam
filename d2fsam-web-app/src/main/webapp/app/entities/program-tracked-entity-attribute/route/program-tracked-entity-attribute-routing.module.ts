import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramTrackedEntityAttributeComponent } from '../list/program-tracked-entity-attribute.component';
import { ProgramTrackedEntityAttributeDetailComponent } from '../detail/program-tracked-entity-attribute-detail.component';
import { ProgramTrackedEntityAttributeUpdateComponent } from '../update/program-tracked-entity-attribute-update.component';
import { ProgramTrackedEntityAttributeRoutingResolveService } from './program-tracked-entity-attribute-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programTrackedEntityAttributeRoute: Routes = [
  {
    path: '',
    component: ProgramTrackedEntityAttributeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramTrackedEntityAttributeDetailComponent,
    resolve: {
      programTrackedEntityAttribute: ProgramTrackedEntityAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramTrackedEntityAttributeUpdateComponent,
    resolve: {
      programTrackedEntityAttribute: ProgramTrackedEntityAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramTrackedEntityAttributeUpdateComponent,
    resolve: {
      programTrackedEntityAttribute: ProgramTrackedEntityAttributeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programTrackedEntityAttributeRoute)],
  exports: [RouterModule],
})
export class ProgramTrackedEntityAttributeRoutingModule {}
