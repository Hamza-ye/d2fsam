import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RelativePeriodsComponent } from './list/relative-periods.component';
import { RelativePeriodsDetailComponent } from './detail/relative-periods-detail.component';
import { RelativePeriodsUpdateComponent } from './update/relative-periods-update.component';
import { RelativePeriodsDeleteDialogComponent } from './delete/relative-periods-delete-dialog.component';
import { RelativePeriodsRoutingModule } from './route/relative-periods-routing.module';

@NgModule({
  imports: [SharedModule, RelativePeriodsRoutingModule],
  declarations: [
    RelativePeriodsComponent,
    RelativePeriodsDetailComponent,
    RelativePeriodsUpdateComponent,
    RelativePeriodsDeleteDialogComponent,
  ],
})
export class RelativePeriodsModule {}
