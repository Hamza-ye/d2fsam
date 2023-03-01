import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramComponent } from './list/program.component';
import { ProgramDetailComponent } from './detail/program-detail.component';
import { ProgramUpdateComponent } from './update/program-update.component';
import { ProgramDeleteDialogComponent } from './delete/program-delete-dialog.component';
import { ProgramRoutingModule } from './route/program-routing.module';

@NgModule({
  imports: [SharedModule, ProgramRoutingModule],
  declarations: [ProgramComponent, ProgramDetailComponent, ProgramUpdateComponent, ProgramDeleteDialogComponent],
})
export class ProgramModule {}
