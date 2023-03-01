import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramStageInstanceComponent } from './list/program-stage-instance.component';
import { ProgramStageInstanceDetailComponent } from './detail/program-stage-instance-detail.component';
import { ProgramStageInstanceUpdateComponent } from './update/program-stage-instance-update.component';
import { ProgramStageInstanceDeleteDialogComponent } from './delete/program-stage-instance-delete-dialog.component';
import { ProgramStageInstanceRoutingModule } from './route/program-stage-instance-routing.module';

@NgModule({
  imports: [SharedModule, ProgramStageInstanceRoutingModule],
  declarations: [
    ProgramStageInstanceComponent,
    ProgramStageInstanceDetailComponent,
    ProgramStageInstanceUpdateComponent,
    ProgramStageInstanceDeleteDialogComponent,
  ],
})
export class ProgramStageInstanceModule {}
