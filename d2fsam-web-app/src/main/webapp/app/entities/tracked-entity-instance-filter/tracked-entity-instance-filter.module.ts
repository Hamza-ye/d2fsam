import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TrackedEntityInstanceFilterComponent } from './list/tracked-entity-instance-filter.component';
import { TrackedEntityInstanceFilterDetailComponent } from './detail/tracked-entity-instance-filter-detail.component';
import { TrackedEntityInstanceFilterUpdateComponent } from './update/tracked-entity-instance-filter-update.component';
import { TrackedEntityInstanceFilterDeleteDialogComponent } from './delete/tracked-entity-instance-filter-delete-dialog.component';
import { TrackedEntityInstanceFilterRoutingModule } from './route/tracked-entity-instance-filter-routing.module';

@NgModule({
  imports: [SharedModule, TrackedEntityInstanceFilterRoutingModule],
  declarations: [
    TrackedEntityInstanceFilterComponent,
    TrackedEntityInstanceFilterDetailComponent,
    TrackedEntityInstanceFilterUpdateComponent,
    TrackedEntityInstanceFilterDeleteDialogComponent,
  ],
})
export class TrackedEntityInstanceFilterModule {}
