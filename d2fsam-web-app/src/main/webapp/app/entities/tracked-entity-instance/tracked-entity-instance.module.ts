import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityInstanceComponent } from './list/tracked-entity-instance.component';
import { TrackedEntityInstanceDetailComponent } from './detail/tracked-entity-instance-detail.component';
import { TrackedEntityInstanceUpdateComponent } from './update/tracked-entity-instance-update.component';
import { TrackedEntityInstanceDeleteDialogComponent } from './delete/tracked-entity-instance-delete-dialog.component';
import { TrackedEntityInstanceRoutingModule } from './route/tracked-entity-instance-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityInstanceRoutingModule],
  declarations: [
    TrackedEntityInstanceComponent,
    TrackedEntityInstanceDetailComponent,
    TrackedEntityInstanceUpdateComponent,
    TrackedEntityInstanceDeleteDialogComponent,
  ],
})
export class TrackedEntityInstanceModule {}
