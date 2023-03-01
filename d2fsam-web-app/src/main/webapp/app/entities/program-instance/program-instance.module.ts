import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramInstanceComponent } from './list/program-instance.component';
import { ProgramInstanceDetailComponent } from './detail/program-instance-detail.component';
import { ProgramInstanceUpdateComponent } from './update/program-instance-update.component';
import { ProgramInstanceDeleteDialogComponent } from './delete/program-instance-delete-dialog.component';
import { ProgramInstanceRoutingModule } from './route/program-instance-routing.module';

@NgModule({
  imports: [SharedModule, ProgramInstanceRoutingModule],
  declarations: [
    ProgramInstanceComponent,
    ProgramInstanceDetailComponent,
    ProgramInstanceUpdateComponent,
    ProgramInstanceDeleteDialogComponent,
  ],
})
export class ProgramInstanceModule {}
