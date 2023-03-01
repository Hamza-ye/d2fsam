import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MalariaCaseComponent } from './list/malaria-case.component';
import { MalariaCaseDetailComponent } from './detail/malaria-case-detail.component';
import { MalariaCaseUpdateComponent } from './update/malaria-case-update.component';
import { MalariaCaseDeleteDialogComponent } from './delete/malaria-case-delete-dialog.component';
import { MalariaCaseRoutingModule } from './route/malaria-case-routing.module';

@NgModule({
  imports: [SharedModule, MalariaCaseRoutingModule],
  declarations: [MalariaCaseComponent, MalariaCaseDetailComponent, MalariaCaseUpdateComponent, MalariaCaseDeleteDialogComponent],
})
export class MalariaCaseModule {}
