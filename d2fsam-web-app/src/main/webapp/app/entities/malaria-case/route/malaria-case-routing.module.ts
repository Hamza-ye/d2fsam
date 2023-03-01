import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MalariaCaseComponent } from '../list/malaria-case.component';
import { MalariaCaseDetailComponent } from '../detail/malaria-case-detail.component';
import { MalariaCaseUpdateComponent } from '../update/malaria-case-update.component';
import { MalariaCaseRoutingResolveService } from './malaria-case-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const malariaCaseRoute: Routes = [
  {
    path: '',
    component: MalariaCaseComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MalariaCaseDetailComponent,
    resolve: {
      malariaCase: MalariaCaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MalariaCaseUpdateComponent,
    resolve: {
      malariaCase: MalariaCaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MalariaCaseUpdateComponent,
    resolve: {
      malariaCase: MalariaCaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(malariaCaseRoute)],
  exports: [RouterModule],
})
export class MalariaCaseRoutingModule {}
