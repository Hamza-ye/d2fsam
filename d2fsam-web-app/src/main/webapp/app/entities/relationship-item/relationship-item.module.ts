import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RelationshipItemComponent } from './list/relationship-item.component';
import { RelationshipItemDetailComponent } from './detail/relationship-item-detail.component';
import { RelationshipItemUpdateComponent } from './update/relationship-item-update.component';
import { RelationshipItemDeleteDialogComponent } from './delete/relationship-item-delete-dialog.component';
import { RelationshipItemRoutingModule } from './route/relationship-item-routing.module';

@NgModule({
  imports: [SharedModule, RelationshipItemRoutingModule],
  declarations: [
    RelationshipItemComponent,
    RelationshipItemDetailComponent,
    RelationshipItemUpdateComponent,
    RelationshipItemDeleteDialogComponent,
  ],
})
export class RelationshipItemModule {}
