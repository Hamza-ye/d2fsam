import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityAttributeValueAuditComponent } from './list/tracked-entity-attribute-value-audit.component';
import { TrackedEntityAttributeValueAuditDetailComponent } from './detail/tracked-entity-attribute-value-audit-detail.component';
import { TrackedEntityAttributeValueAuditUpdateComponent } from './update/tracked-entity-attribute-value-audit-update.component';
import { TrackedEntityAttributeValueAuditDeleteDialogComponent } from './delete/tracked-entity-attribute-value-audit-delete-dialog.component';
import { TrackedEntityAttributeValueAuditRoutingModule } from './route/tracked-entity-attribute-value-audit-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityAttributeValueAuditRoutingModule],
  declarations: [
    TrackedEntityAttributeValueAuditComponent,
    TrackedEntityAttributeValueAuditDetailComponent,
    TrackedEntityAttributeValueAuditUpdateComponent,
    TrackedEntityAttributeValueAuditDeleteDialogComponent,
  ],
})
export class TrackedEntityAttributeValueAuditModule {}
