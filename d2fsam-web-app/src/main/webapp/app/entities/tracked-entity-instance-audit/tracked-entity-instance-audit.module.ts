import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityInstanceAuditComponent } from './list/tracked-entity-instance-audit.component';
import { TrackedEntityInstanceAuditDetailComponent } from './detail/tracked-entity-instance-audit-detail.component';
import { TrackedEntityInstanceAuditUpdateComponent } from './update/tracked-entity-instance-audit-update.component';
import { TrackedEntityInstanceAuditDeleteDialogComponent } from './delete/tracked-entity-instance-audit-delete-dialog.component';
import { TrackedEntityInstanceAuditRoutingModule } from './route/tracked-entity-instance-audit-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityInstanceAuditRoutingModule],
  declarations: [
    TrackedEntityInstanceAuditComponent,
    TrackedEntityInstanceAuditDetailComponent,
    TrackedEntityInstanceAuditUpdateComponent,
    TrackedEntityInstanceAuditDeleteDialogComponent,
  ],
})
export class TrackedEntityInstanceAuditModule {}
