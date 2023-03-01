import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RelationshipTypeComponent } from './list/relationship-type.component';
import { RelationshipTypeDetailComponent } from './detail/relationship-type-detail.component';
import { RelationshipTypeUpdateComponent } from './update/relationship-type-update.component';
import { RelationshipTypeDeleteDialogComponent } from './delete/relationship-type-delete-dialog.component';
import { RelationshipTypeRoutingModule } from './route/relationship-type-routing.module';

@NgModule({
  imports: [SharedModule, RelationshipTypeRoutingModule],
  declarations: [
    RelationshipTypeComponent,
    RelationshipTypeDetailComponent,
    RelationshipTypeUpdateComponent,
    RelationshipTypeDeleteDialogComponent,
  ],
})
export class RelationshipTypeModule {}
