import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DemographicDataSourceComponent } from './list/demographic-data-source.component';
import { DemographicDataSourceDetailComponent } from './detail/demographic-data-source-detail.component';
import { DemographicDataSourceUpdateComponent } from './update/demographic-data-source-update.component';
import { DemographicDataSourceDeleteDialogComponent } from './delete/demographic-data-source-delete-dialog.component';
import { DemographicDataSourceRoutingModule } from './route/demographic-data-source-routing.module';

@NgModule({
  imports: [SharedModule, DemographicDataSourceRoutingModule],
  declarations: [
    DemographicDataSourceComponent,
    DemographicDataSourceDetailComponent,
    DemographicDataSourceUpdateComponent,
    DemographicDataSourceDeleteDialogComponent,
  ],
})
export class DemographicDataSourceModule {}
