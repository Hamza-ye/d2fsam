import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrganisationUnitLevelComponent } from '../list/organisation-unit-level.component';
import { OrganisationUnitLevelDetailComponent } from '../detail/organisation-unit-level-detail.component';
import { OrganisationUnitLevelUpdateComponent } from '../update/organisation-unit-level-update.component';
import { OrganisationUnitLevelRoutingResolveService } from './organisation-unit-level-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const organisationUnitLevelRoute: Routes = [
  {
    path: '',
    component: OrganisationUnitLevelComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrganisationUnitLevelDetailComponent,
    resolve: {
      organisationUnitLevel: OrganisationUnitLevelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrganisationUnitLevelUpdateComponent,
    resolve: {
      organisationUnitLevel: OrganisationUnitLevelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrganisationUnitLevelUpdateComponent,
    resolve: {
      organisationUnitLevel: OrganisationUnitLevelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(organisationUnitLevelRoute)],
  exports: [RouterModule],
})
export class OrganisationUnitLevelRoutingModule {}
