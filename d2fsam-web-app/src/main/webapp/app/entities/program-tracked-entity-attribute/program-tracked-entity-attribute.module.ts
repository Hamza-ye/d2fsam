import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramTrackedEntityAttributeComponent } from './list/program-tracked-entity-attribute.component';
import { ProgramTrackedEntityAttributeDetailComponent } from './detail/program-tracked-entity-attribute-detail.component';
import { ProgramTrackedEntityAttributeUpdateComponent } from './update/program-tracked-entity-attribute-update.component';
import { ProgramTrackedEntityAttributeDeleteDialogComponent } from './delete/program-tracked-entity-attribute-delete-dialog.component';
import { ProgramTrackedEntityAttributeRoutingModule } from './route/program-tracked-entity-attribute-routing.module';

@NgModule({
  imports: [SharedModule, ProgramTrackedEntityAttributeRoutingModule],
  declarations: [
    ProgramTrackedEntityAttributeComponent,
    ProgramTrackedEntityAttributeDetailComponent,
    ProgramTrackedEntityAttributeUpdateComponent,
    ProgramTrackedEntityAttributeDeleteDialogComponent,
  ],
})
export class ProgramTrackedEntityAttributeModule {}
