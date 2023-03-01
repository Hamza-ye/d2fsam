import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramInstance } from '../program-instance.model';
import { ProgramInstanceService } from '../service/program-instance.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-instance-delete-dialog.component.html',
})
export class ProgramInstanceDeleteDialogComponent {
  programInstance?: IProgramInstance;

  constructor(protected programInstanceService: ProgramInstanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programInstanceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
