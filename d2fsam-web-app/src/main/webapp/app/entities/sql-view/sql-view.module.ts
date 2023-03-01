import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SqlViewComponent } from './list/sql-view.component';
import { SqlViewDetailComponent } from './detail/sql-view-detail.component';
import { SqlViewUpdateComponent } from './update/sql-view-update.component';
import { SqlViewDeleteDialogComponent } from './delete/sql-view-delete-dialog.component';
import { SqlViewRoutingModule } from './route/sql-view-routing.module';

@NgModule({
  imports: [SharedModule, SqlViewRoutingModule],
  declarations: [SqlViewComponent, SqlViewDetailComponent, SqlViewUpdateComponent, SqlViewDeleteDialogComponent],
})
export class SqlViewModule {}
