import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SqlViewComponent } from '../list/sql-view.component';
import { SqlViewDetailComponent } from '../detail/sql-view-detail.component';
import { SqlViewUpdateComponent } from '../update/sql-view-update.component';
import { SqlViewRoutingResolveService } from './sql-view-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const sqlViewRoute: Routes = [
  {
    path: '',
    component: SqlViewComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SqlViewDetailComponent,
    resolve: {
      sqlView: SqlViewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SqlViewUpdateComponent,
    resolve: {
      sqlView: SqlViewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SqlViewUpdateComponent,
    resolve: {
      sqlView: SqlViewRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(sqlViewRoute)],
  exports: [RouterModule],
})
export class SqlViewRoutingModule {}
