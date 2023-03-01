import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OptionSetComponent } from './list/option-set.component';
import { OptionSetDetailComponent } from './detail/option-set-detail.component';
import { OptionSetUpdateComponent } from './update/option-set-update.component';
import { OptionSetDeleteDialogComponent } from './delete/option-set-delete-dialog.component';
import { OptionSetRoutingModule } from './route/option-set-routing.module';

@NgModule({
  imports: [SharedModule, OptionSetRoutingModule],
  declarations: [OptionSetComponent, OptionSetDetailComponent, OptionSetUpdateComponent, OptionSetDeleteDialogComponent],
})
export class OptionSetModule {}
