import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramStageDataElementComponent } from './list/program-stage-data-element.component';
import { ProgramStageDataElementDetailComponent } from './detail/program-stage-data-element-detail.component';
import { ProgramStageDataElementUpdateComponent } from './update/program-stage-data-element-update.component';
import { ProgramStageDataElementDeleteDialogComponent } from './delete/program-stage-data-element-delete-dialog.component';
import { ProgramStageDataElementRoutingModule } from './route/program-stage-data-element-routing.module';

@NgModule({
  imports: [SharedModule, ProgramStageDataElementRoutingModule],
  declarations: [
    ProgramStageDataElementComponent,
    ProgramStageDataElementDetailComponent,
    ProgramStageDataElementUpdateComponent,
    ProgramStageDataElementDeleteDialogComponent,
  ],
})
export class ProgramStageDataElementModule {}
