import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrganisationUnitComponent } from '../list/organisation-unit.component';
import { OrganisationUnitDetailComponent } from '../detail/organisation-unit-detail.component';
import { OrganisationUnitUpdateComponent } from '../update/organisation-unit-update.component';
import { OrganisationUnitRoutingResolveService } from './organisation-unit-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const organisationUnitRoute: Routes = [
  {
    path: '',
    component: OrganisationUnitComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrganisationUnitDetailComponent,
    resolve: {
      organisationUnit: OrganisationUnitRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrganisationUnitUpdateComponent,
    resolve: {
      organisationUnit: OrganisationUnitRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrganisationUnitUpdateComponent,
    resolve: {
      organisationUnit: OrganisationUnitRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(organisationUnitRoute)],
  exports: [RouterModule],
})
export class OrganisationUnitRoutingModule {}
