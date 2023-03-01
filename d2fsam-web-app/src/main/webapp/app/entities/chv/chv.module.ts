import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ChvComponent } from './list/chv.component';
import { ChvDetailComponent } from './detail/chv-detail.component';
import { ChvUpdateComponent } from './update/chv-update.component';
import { ChvDeleteDialogComponent } from './delete/chv-delete-dialog.component';
import { ChvRoutingModule } from './route/chv-routing.module';

@NgModule({
  imports: [SharedModule, ChvRoutingModule],
  declarations: [ChvComponent, ChvDetailComponent, ChvUpdateComponent, ChvDeleteDialogComponent],
})
export class ChvModule {}
