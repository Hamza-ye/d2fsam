import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DataElementComponent } from './list/data-element.component';
import { DataElementDetailComponent } from './detail/data-element-detail.component';
import { DataElementUpdateComponent } from './update/data-element-update.component';
import { DataElementDeleteDialogComponent } from './delete/data-element-delete-dialog.component';
import { DataElementRoutingModule } from './route/data-element-routing.module';

@NgModule({
  imports: [SharedModule, DataElementRoutingModule],
  declarations: [DataElementComponent, DataElementDetailComponent, DataElementUpdateComponent, DataElementDeleteDialogComponent],
})
export class DataElementModule {}
