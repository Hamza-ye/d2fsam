import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AssignmentComponent } from './list/assignment.component';
import { AssignmentDetailComponent } from './detail/assignment-detail.component';
import { AssignmentUpdateComponent } from './update/assignment-update.component';
import { AssignmentDeleteDialogComponent } from './delete/assignment-delete-dialog.component';
import { AssignmentRoutingModule } from './route/assignment-routing.module';

@NgModule({
  imports: [SharedModule, AssignmentRoutingModule],
  declarations: [AssignmentComponent, AssignmentDetailComponent, AssignmentUpdateComponent, AssignmentDeleteDialogComponent],
})
export class AssignmentModule {}
