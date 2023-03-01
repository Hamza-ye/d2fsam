import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPeriodType } from '../period-type.model';
import { PeriodTypeService } from '../service/period-type.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './period-type-delete-dialog.component.html',
})
export class PeriodTypeDeleteDialogComponent {
  periodType?: IPeriodType;

  constructor(protected periodTypeService: PeriodTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.periodTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
