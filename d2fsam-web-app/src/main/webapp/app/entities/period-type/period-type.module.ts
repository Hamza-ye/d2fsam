import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PeriodTypeComponent } from './list/period-type.component';
import { PeriodTypeDetailComponent } from './detail/period-type-detail.component';
import { PeriodTypeUpdateComponent } from './update/period-type-update.component';
import { PeriodTypeDeleteDialogComponent } from './delete/period-type-delete-dialog.component';
import { PeriodTypeRoutingModule } from './route/period-type-routing.module';

@NgModule({
  imports: [SharedModule, PeriodTypeRoutingModule],
  declarations: [PeriodTypeComponent, PeriodTypeDetailComponent, PeriodTypeUpdateComponent, PeriodTypeDeleteDialogComponent],
})
export class PeriodTypeModule {}
