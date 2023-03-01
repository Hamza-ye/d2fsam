import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RelationshipComponent } from './list/relationship.component';
import { RelationshipDetailComponent } from './detail/relationship-detail.component';
import { RelationshipUpdateComponent } from './update/relationship-update.component';
import { RelationshipDeleteDialogComponent } from './delete/relationship-delete-dialog.component';
import { RelationshipRoutingModule } from './route/relationship-routing.module';

@NgModule({
  imports: [SharedModule, RelationshipRoutingModule],
  declarations: [RelationshipComponent, RelationshipDetailComponent, RelationshipUpdateComponent, RelationshipDeleteDialogComponent],
})
export class RelationshipModule {}
