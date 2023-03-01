import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FileResourceComponent } from '../list/file-resource.component';
import { FileResourceDetailComponent } from '../detail/file-resource-detail.component';
import { FileResourceUpdateComponent } from '../update/file-resource-update.component';
import { FileResourceRoutingResolveService } from './file-resource-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const fileResourceRoute: Routes = [
  {
    path: '',
    component: FileResourceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FileResourceDetailComponent,
    resolve: {
      fileResource: FileResourceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FileResourceUpdateComponent,
    resolve: {
      fileResource: FileResourceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FileResourceUpdateComponent,
    resolve: {
      fileResource: FileResourceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fileResourceRoute)],
  exports: [RouterModule],
})
export class FileResourceRoutingModule {}
