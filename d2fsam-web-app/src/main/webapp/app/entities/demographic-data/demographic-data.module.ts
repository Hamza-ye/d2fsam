import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DemographicDataComponent } from './list/demographic-data.component';
import { DemographicDataDetailComponent } from './detail/demographic-data-detail.component';
import { DemographicDataUpdateComponent } from './update/demographic-data-update.component';
import { DemographicDataDeleteDialogComponent } from './delete/demographic-data-delete-dialog.component';
import { DemographicDataRoutingModule } from './route/demographic-data-routing.module';

@NgModule({
  imports: [SharedModule, DemographicDataRoutingModule],
  declarations: [
    DemographicDataComponent,
    DemographicDataDetailComponent,
    DemographicDataUpdateComponent,
    DemographicDataDeleteDialogComponent,
  ],
})
export class DemographicDataModule {}
