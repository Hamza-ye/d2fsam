import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ExternalFileResourceComponent } from '../list/external-file-resource.component';
import { ExternalFileResourceDetailComponent } from '../detail/external-file-resource-detail.component';
import { ExternalFileResourceUpdateComponent } from '../update/external-file-resource-update.component';
import { ExternalFileResourceRoutingResolveService } from './external-file-resource-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const externalFileResourceRoute: Routes = [
  {
    path: '',
    component: ExternalFileResourceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExternalFileResourceDetailComponent,
    resolve: {
      externalFileResource: ExternalFileResourceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExternalFileResourceUpdateComponent,
    resolve: {
      externalFileResource: ExternalFileResourceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExternalFileResourceUpdateComponent,
    resolve: {
      externalFileResource: ExternalFileResourceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(externalFileResourceRoute)],
  exports: [RouterModule],
})
export class ExternalFileResourceRoutingModule {}
