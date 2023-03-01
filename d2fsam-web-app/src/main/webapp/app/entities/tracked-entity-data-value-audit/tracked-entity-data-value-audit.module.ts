import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityDataValueAuditComponent } from './list/tracked-entity-data-value-audit.component';
import { TrackedEntityDataValueAuditDetailComponent } from './detail/tracked-entity-data-value-audit-detail.component';
import { TrackedEntityDataValueAuditUpdateComponent } from './update/tracked-entity-data-value-audit-update.component';
import { TrackedEntityDataValueAuditDeleteDialogComponent } from './delete/tracked-entity-data-value-audit-delete-dialog.component';
import { TrackedEntityDataValueAuditRoutingModule } from './route/tracked-entity-data-value-audit-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityDataValueAuditRoutingModule],
  declarations: [
    TrackedEntityDataValueAuditComponent,
    TrackedEntityDataValueAuditDetailComponent,
    TrackedEntityDataValueAuditUpdateComponent,
    TrackedEntityDataValueAuditDeleteDialogComponent,
  ],
})
export class TrackedEntityDataValueAuditModule {}
