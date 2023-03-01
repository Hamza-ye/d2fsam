import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDataInputPeriod } from '../data-input-period.model';
import { DataInputPeriodService } from '../service/data-input-period.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './data-input-period-delete-dialog.component.html',
})
export class DataInputPeriodDeleteDialogComponent {
  dataInputPeriod?: IDataInputPeriod;

  constructor(protected dataInputPeriodService: DataInputPeriodService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dataInputPeriodService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
