import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramOwnershipHistory } from '../program-ownership-history.model';
import { ProgramOwnershipHistoryService } from '../service/program-ownership-history.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-ownership-history-delete-dialog.component.html',
})
export class ProgramOwnershipHistoryDeleteDialogComponent {
  programOwnershipHistory?: IProgramOwnershipHistory;

  constructor(protected programOwnershipHistoryService: ProgramOwnershipHistoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programOwnershipHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
