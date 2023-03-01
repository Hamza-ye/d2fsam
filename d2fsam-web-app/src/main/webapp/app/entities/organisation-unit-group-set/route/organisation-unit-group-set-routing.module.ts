import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrganisationUnitGroupSetComponent } from '../list/organisation-unit-group-set.component';
import { OrganisationUnitGroupSetDetailComponent } from '../detail/organisation-unit-group-set-detail.component';
import { OrganisationUnitGroupSetUpdateComponent } from '../update/organisation-unit-group-set-update.component';
import { OrganisationUnitGroupSetRoutingResolveService } from './organisation-unit-group-set-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const organisationUnitGroupSetRoute: Routes = [
  {
    path: '',
    component: OrganisationUnitGroupSetComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrganisationUnitGroupSetDetailComponent,
    resolve: {
      organisationUnitGroupSet: OrganisationUnitGroupSetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrganisationUnitGroupSetUpdateComponent,
    resolve: {
      organisationUnitGroupSet: OrganisationUnitGroupSetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrganisationUnitGroupSetUpdateComponent,
    resolve: {
      organisationUnitGroupSet: OrganisationUnitGroupSetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(organisationUnitGroupSetRoute)],
  exports: [RouterModule],
})
export class OrganisationUnitGroupSetRoutingModule {}
