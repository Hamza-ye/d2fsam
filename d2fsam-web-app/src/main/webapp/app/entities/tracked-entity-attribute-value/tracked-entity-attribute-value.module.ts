import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityAttributeValueComponent } from './list/tracked-entity-attribute-value.component';
import { TrackedEntityAttributeValueDetailComponent } from './detail/tracked-entity-attribute-value-detail.component';
import { TrackedEntityAttributeValueUpdateComponent } from './update/tracked-entity-attribute-value-update.component';
import { TrackedEntityAttributeValueDeleteDialogComponent } from './delete/tracked-entity-attribute-value-delete-dialog.component';
import { TrackedEntityAttributeValueRoutingModule } from './route/tracked-entity-attribute-value-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityAttributeValueRoutingModule],
  declarations: [
    TrackedEntityAttributeValueComponent,
    TrackedEntityAttributeValueDetailComponent,
    TrackedEntityAttributeValueUpdateComponent,
    TrackedEntityAttributeValueDeleteDialogComponent,
  ],
})
export class TrackedEntityAttributeValueModule {}
