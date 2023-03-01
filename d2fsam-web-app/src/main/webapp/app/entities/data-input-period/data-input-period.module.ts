import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DataInputPeriodComponent } from './list/data-input-period.component';
import { DataInputPeriodDetailComponent } from './detail/data-input-period-detail.component';
import { DataInputPeriodUpdateComponent } from './update/data-input-period-update.component';
import { DataInputPeriodDeleteDialogComponent } from './delete/data-input-period-delete-dialog.component';
import { DataInputPeriodRoutingModule } from './route/data-input-period-routing.module';

@NgModule({
  imports: [SharedModule, DataInputPeriodRoutingModule],
  declarations: [
    DataInputPeriodComponent,
    DataInputPeriodDetailComponent,
    DataInputPeriodUpdateComponent,
    DataInputPeriodDeleteDialogComponent,
  ],
})
export class DataInputPeriodModule {}
