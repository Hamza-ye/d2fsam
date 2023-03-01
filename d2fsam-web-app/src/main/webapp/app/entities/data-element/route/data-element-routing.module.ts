import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DataElementComponent } from '../list/data-element.component';
import { DataElementDetailComponent } from '../detail/data-element-detail.component';
import { DataElementUpdateComponent } from '../update/data-element-update.component';
import { DataElementRoutingResolveService } from './data-element-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const dataElementRoute: Routes = [
  {
    path: '',
    component: DataElementComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DataElementDetailComponent,
    resolve: {
      dataElement: DataElementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DataElementUpdateComponent,
    resolve: {
      dataElement: DataElementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DataElementUpdateComponent,
    resolve: {
      dataElement: DataElementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dataElementRoute)],
  exports: [RouterModule],
})
export class DataElementRoutingModule {}
