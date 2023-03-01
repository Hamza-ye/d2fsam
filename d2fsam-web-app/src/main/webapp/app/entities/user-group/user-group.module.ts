import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserGroupComponent } from './list/user-group.component';
import { UserGroupDetailComponent } from './detail/user-group-detail.component';
import { UserGroupUpdateComponent } from './update/user-group-update.component';
import { UserGroupDeleteDialogComponent } from './delete/user-group-delete-dialog.component';
import { UserGroupRoutingModule } from './route/user-group-routing.module';

@NgModule({
  imports: [SharedModule, UserGroupRoutingModule],
  declarations: [UserGroupComponent, UserGroupDetailComponent, UserGroupUpdateComponent, UserGroupDeleteDialogComponent],
})
export class UserGroupModule {}
