import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStockItemGroup } from '../stock-item-group.model';
import { StockItemGroupService } from '../service/stock-item-group.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './stock-item-group-delete-dialog.component.html',
})
export class StockItemGroupDeleteDialogComponent {
  stockItemGroup?: IStockItemGroup;

  constructor(protected stockItemGroupService: StockItemGroupService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stockItemGroupService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
