import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MetadataVersionComponent } from '../list/metadata-version.component';
import { MetadataVersionDetailComponent } from '../detail/metadata-version-detail.component';
import { MetadataVersionUpdateComponent } from '../update/metadata-version-update.component';
import { MetadataVersionRoutingResolveService } from './metadata-version-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const metadataVersionRoute: Routes = [
  {
    path: '',
    component: MetadataVersionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MetadataVersionDetailComponent,
    resolve: {
      metadataVersion: MetadataVersionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MetadataVersionUpdateComponent,
    resolve: {
      metadataVersion: MetadataVersionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MetadataVersionUpdateComponent,
    resolve: {
      metadataVersion: MetadataVersionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(metadataVersionRoute)],
  exports: [RouterModule],
})
export class MetadataVersionRoutingModule {}
