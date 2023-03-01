import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProgramOwnershipHistoryComponent } from './list/program-ownership-history.component';
import { ProgramOwnershipHistoryDetailComponent } from './detail/program-ownership-history-detail.component';
import { ProgramOwnershipHistoryUpdateComponent } from './update/program-ownership-history-update.component';
import { ProgramOwnershipHistoryDeleteDialogComponent } from './delete/program-ownership-history-delete-dialog.component';
import { ProgramOwnershipHistoryRoutingModule } from './route/program-ownership-history-routing.module';

@NgModule({
  imports: [SharedModule, ProgramOwnershipHistoryRoutingModule],
  declarations: [
    ProgramOwnershipHistoryComponent,
    ProgramOwnershipHistoryDetailComponent,
    ProgramOwnershipHistoryUpdateComponent,
    ProgramOwnershipHistoryDeleteDialogComponent,
  ],
})
export class ProgramOwnershipHistoryModule {}
