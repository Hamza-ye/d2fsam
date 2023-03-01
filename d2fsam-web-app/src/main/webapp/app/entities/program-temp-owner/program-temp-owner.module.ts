import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramTempOwnerComponent } from './list/program-temp-owner.component';
import { ProgramTempOwnerDetailComponent } from './detail/program-temp-owner-detail.component';
import { ProgramTempOwnerUpdateComponent } from './update/program-temp-owner-update.component';
import { ProgramTempOwnerDeleteDialogComponent } from './delete/program-temp-owner-delete-dialog.component';
import { ProgramTempOwnerRoutingModule } from './route/program-temp-owner-routing.module';

@NgModule({
  imports: [SharedModule, ProgramTempOwnerRoutingModule],
  declarations: [
    ProgramTempOwnerComponent,
    ProgramTempOwnerDetailComponent,
    ProgramTempOwnerUpdateComponent,
    ProgramTempOwnerDeleteDialogComponent,
  ],
})
export class ProgramTempOwnerModule {}
