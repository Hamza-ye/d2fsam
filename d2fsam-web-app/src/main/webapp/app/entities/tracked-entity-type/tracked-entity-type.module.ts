import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityTypeComponent } from './list/tracked-entity-type.component';
import { TrackedEntityTypeDetailComponent } from './detail/tracked-entity-type-detail.component';
import { TrackedEntityTypeUpdateComponent } from './update/tracked-entity-type-update.component';
import { TrackedEntityTypeDeleteDialogComponent } from './delete/tracked-entity-type-delete-dialog.component';
import { TrackedEntityTypeRoutingModule } from './route/tracked-entity-type-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityTypeRoutingModule],
  declarations: [
    TrackedEntityTypeComponent,
    TrackedEntityTypeDetailComponent,
    TrackedEntityTypeUpdateComponent,
    TrackedEntityTypeDeleteDialogComponent,
  ],
})
export class TrackedEntityTypeModule {}
