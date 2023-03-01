import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityProgramOwnerComponent } from './list/tracked-entity-program-owner.component';
import { TrackedEntityProgramOwnerDetailComponent } from './detail/tracked-entity-program-owner-detail.component';
import { TrackedEntityProgramOwnerUpdateComponent } from './update/tracked-entity-program-owner-update.component';
import { TrackedEntityProgramOwnerDeleteDialogComponent } from './delete/tracked-entity-program-owner-delete-dialog.component';
import { TrackedEntityProgramOwnerRoutingModule } from './route/tracked-entity-program-owner-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityProgramOwnerRoutingModule],
  declarations: [
    TrackedEntityProgramOwnerComponent,
    TrackedEntityProgramOwnerDetailComponent,
    TrackedEntityProgramOwnerUpdateComponent,
    TrackedEntityProgramOwnerDeleteDialogComponent,
  ],
})
export class TrackedEntityProgramOwnerModule {}
