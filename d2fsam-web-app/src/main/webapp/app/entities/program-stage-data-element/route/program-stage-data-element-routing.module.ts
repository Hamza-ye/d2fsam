import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProgramStageDataElementComponent } from '../list/program-stage-data-element.component';
import { ProgramStageDataElementDetailComponent } from '../detail/program-stage-data-element-detail.component';
import { ProgramStageDataElementUpdateComponent } from '../update/program-stage-data-element-update.component';
import { ProgramStageDataElementRoutingResolveService } from './program-stage-data-element-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const programStageDataElementRoute: Routes = [
  {
    path: '',
    component: ProgramStageDataElementComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProgramStageDataElementDetailComponent,
    resolve: {
      programStageDataElement: ProgramStageDataElementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProgramStageDataElementUpdateComponent,
    resolve: {
      programStageDataElement: ProgramStageDataElementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProgramStageDataElementUpdateComponent,
    resolve: {
      programStageDataElement: ProgramStageDataElementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(programStageDataElementRoute)],
  exports: [RouterModule],
})
export class ProgramStageDataElementRoutingModule {}
