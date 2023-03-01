import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StockItemGroupComponent } from './list/stock-item-group.component';
import { StockItemGroupDetailComponent } from './detail/stock-item-group-detail.component';
import { StockItemGroupUpdateComponent } from './update/stock-item-group-update.component';
import { StockItemGroupDeleteDialogComponent } from './delete/stock-item-group-delete-dialog.component';
import { StockItemGroupRoutingModule } from './route/stock-item-group-routing.module';

@NgModule({
  imports: [SharedModule, StockItemGroupRoutingModule],
  declarations: [
    StockItemGroupComponent,
    StockItemGroupDetailComponent,
    StockItemGroupUpdateComponent,
    StockItemGroupDeleteDialogComponent,
  ],
})
export class StockItemGroupModule {}
