import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramStageInstanceFilterComponent } from './list/program-stage-instance-filter.component';
import { ProgramStageInstanceFilterDetailComponent } from './detail/program-stage-instance-filter-detail.component';
import { ProgramStageInstanceFilterUpdateComponent } from './update/program-stage-instance-filter-update.component';
import { ProgramStageInstanceFilterDeleteDialogComponent } from './delete/program-stage-instance-filter-delete-dialog.component';
import { ProgramStageInstanceFilterRoutingModule } from './route/program-stage-instance-filter-routing.module';

@NgModule({
  imports: [SharedModule, ProgramStageInstanceFilterRoutingModule],
  declarations: [
    ProgramStageInstanceFilterComponent,
    ProgramStageInstanceFilterDetailComponent,
    ProgramStageInstanceFilterUpdateComponent,
    ProgramStageInstanceFilterDeleteDialogComponent,
  ],
})
export class ProgramStageInstanceFilterModule {}
