import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramStageComponent } from './list/program-stage.component';
import { ProgramStageDetailComponent } from './detail/program-stage-detail.component';
import { ProgramStageUpdateComponent } from './update/program-stage-update.component';
import { ProgramStageDeleteDialogComponent } from './delete/program-stage-delete-dialog.component';
import { ProgramStageRoutingModule } from './route/program-stage-routing.module';

@NgModule({
  imports: [SharedModule, ProgramStageRoutingModule],
  declarations: [ProgramStageComponent, ProgramStageDetailComponent, ProgramStageUpdateComponent, ProgramStageDeleteDialogComponent],
})
export class ProgramStageModule {}
