import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DatastoreEntryComponent } from '../list/datastore-entry.component';
import { DatastoreEntryDetailComponent } from '../detail/datastore-entry-detail.component';
import { DatastoreEntryUpdateComponent } from '../update/datastore-entry-update.component';
import { DatastoreEntryRoutingResolveService } from './datastore-entry-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const datastoreEntryRoute: Routes = [
  {
    path: '',
    component: DatastoreEntryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DatastoreEntryDetailComponent,
    resolve: {
      datastoreEntry: DatastoreEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DatastoreEntryUpdateComponent,
    resolve: {
      datastoreEntry: DatastoreEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DatastoreEntryUpdateComponent,
    resolve: {
      datastoreEntry: DatastoreEntryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(datastoreEntryRoute)],
  exports: [RouterModule],
})
export class DatastoreEntryRoutingModule {}
