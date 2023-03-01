import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DatastoreEntryComponent } from './list/datastore-entry.component';
import { DatastoreEntryDetailComponent } from './detail/datastore-entry-detail.component';
import { DatastoreEntryUpdateComponent } from './update/datastore-entry-update.component';
import { DatastoreEntryDeleteDialogComponent } from './delete/datastore-entry-delete-dialog.component';
import { DatastoreEntryRoutingModule } from './route/datastore-entry-routing.module';

@NgModule({
  imports: [SharedModule, DatastoreEntryRoutingModule],
  declarations: [
    DatastoreEntryComponent,
    DatastoreEntryDetailComponent,
    DatastoreEntryUpdateComponent,
    DatastoreEntryDeleteDialogComponent,
  ],
})
export class DatastoreEntryModule {}
