import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityTypeAttributeComponent } from './list/tracked-entity-type-attribute.component';
import { TrackedEntityTypeAttributeDetailComponent } from './detail/tracked-entity-type-attribute-detail.component';
import { TrackedEntityTypeAttributeUpdateComponent } from './update/tracked-entity-type-attribute-update.component';
import { TrackedEntityTypeAttributeDeleteDialogComponent } from './delete/tracked-entity-type-attribute-delete-dialog.component';
import { TrackedEntityTypeAttributeRoutingModule } from './route/tracked-entity-type-attribute-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityTypeAttributeRoutingModule],
  declarations: [
    TrackedEntityTypeAttributeComponent,
    TrackedEntityTypeAttributeDetailComponent,
    TrackedEntityTypeAttributeUpdateComponent,
    TrackedEntityTypeAttributeDeleteDialogComponent,
  ],
})
export class TrackedEntityTypeAttributeModule {}
