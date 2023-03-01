import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserAuthorityGroupComponent } from './list/user-authority-group.component';
import { UserAuthorityGroupDetailComponent } from './detail/user-authority-group-detail.component';
import { UserAuthorityGroupUpdateComponent } from './update/user-authority-group-update.component';
import { UserAuthorityGroupDeleteDialogComponent } from './delete/user-authority-group-delete-dialog.component';
import { UserAuthorityGroupRoutingModule } from './route/user-authority-group-routing.module';

@NgModule({
  imports: [SharedModule, UserAuthorityGroupRoutingModule],
  declarations: [
    UserAuthorityGroupComponent,
    UserAuthorityGroupDetailComponent,
    UserAuthorityGroupUpdateComponent,
    UserAuthorityGroupDeleteDialogComponent,
  ],
})
export class UserAuthorityGroupModule {}
