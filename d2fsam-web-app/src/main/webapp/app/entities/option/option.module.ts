import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OptionComponent } from './list/option.component';
import { OptionDetailComponent } from './detail/option-detail.component';
import { OptionUpdateComponent } from './update/option-update.component';
import { OptionDeleteDialogComponent } from './delete/option-delete-dialog.component';
import { OptionRoutingModule } from './route/option-routing.module';

@NgModule({
  imports: [SharedModule, OptionRoutingModule],
  declarations: [OptionComponent, OptionDetailComponent, OptionUpdateComponent, OptionDeleteDialogComponent],
})
export class OptionModule {}
