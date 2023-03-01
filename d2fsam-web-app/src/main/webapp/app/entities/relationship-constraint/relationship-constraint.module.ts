import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RelationshipConstraintComponent } from './list/relationship-constraint.component';
import { RelationshipConstraintDetailComponent } from './detail/relationship-constraint-detail.component';
import { RelationshipConstraintUpdateComponent } from './update/relationship-constraint-update.component';
import { RelationshipConstraintDeleteDialogComponent } from './delete/relationship-constraint-delete-dialog.component';
import { RelationshipConstraintRoutingModule } from './route/relationship-constraint-routing.module';

@NgModule({
  imports: [SharedModule, RelationshipConstraintRoutingModule],
  declarations: [
    RelationshipConstraintComponent,
    RelationshipConstraintDetailComponent,
    RelationshipConstraintUpdateComponent,
    RelationshipConstraintDeleteDialogComponent,
  ],
})
export class RelationshipConstraintModule {}
