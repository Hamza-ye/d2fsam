import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRelativePeriods } from '../relative-periods.model';
import { RelativePeriodsService } from '../service/relative-periods.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './relative-periods-delete-dialog.component.html',
})
export class RelativePeriodsDeleteDialogComponent {
  relativePeriods?: IRelativePeriods;

  constructor(protected relativePeriodsService: RelativePeriodsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.relativePeriodsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
