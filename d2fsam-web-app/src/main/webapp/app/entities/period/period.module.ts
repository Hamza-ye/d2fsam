import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PeriodComponent } from './list/period.component';
import { PeriodDetailComponent } from './detail/period-detail.component';
import { PeriodUpdateComponent } from './update/period-update.component';
import { PeriodDeleteDialogComponent } from './delete/period-delete-dialog.component';
import { PeriodRoutingModule } from './route/period-routing.module';

@NgModule({
  imports: [SharedModule, PeriodRoutingModule],
  declarations: [PeriodComponent, PeriodDetailComponent, PeriodUpdateComponent, PeriodDeleteDialogComponent],
})
export class PeriodModule {}
