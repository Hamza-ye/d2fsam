import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DataValueComponent } from './list/data-value.component';
import { DataValueDetailComponent } from './detail/data-value-detail.component';
import { DataValueUpdateComponent } from './update/data-value-update.component';
import { DataValueDeleteDialogComponent } from './delete/data-value-delete-dialog.component';
import { DataValueRoutingModule } from './route/data-value-routing.module';

@NgModule({
  imports: [SharedModule, DataValueRoutingModule],
  declarations: [DataValueComponent, DataValueDetailComponent, DataValueUpdateComponent, DataValueDeleteDialogComponent],
})
export class DataValueModule {}
