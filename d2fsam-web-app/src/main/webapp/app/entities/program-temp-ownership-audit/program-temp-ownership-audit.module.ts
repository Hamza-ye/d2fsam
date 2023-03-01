import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramTempOwnershipAuditComponent } from './list/program-temp-ownership-audit.component';
import { ProgramTempOwnershipAuditDetailComponent } from './detail/program-temp-ownership-audit-detail.component';
import { ProgramTempOwnershipAuditUpdateComponent } from './update/program-temp-ownership-audit-update.component';
import { ProgramTempOwnershipAuditDeleteDialogComponent } from './delete/program-temp-ownership-audit-delete-dialog.component';
import { ProgramTempOwnershipAuditRoutingModule } from './route/program-temp-ownership-audit-routing.module';

@NgModule({
  imports: [SharedModule, ProgramTempOwnershipAuditRoutingModule],
  declarations: [
    ProgramTempOwnershipAuditComponent,
    ProgramTempOwnershipAuditDetailComponent,
    ProgramTempOwnershipAuditUpdateComponent,
    ProgramTempOwnershipAuditDeleteDialogComponent,
  ],
})
export class ProgramTempOwnershipAuditModule {}
