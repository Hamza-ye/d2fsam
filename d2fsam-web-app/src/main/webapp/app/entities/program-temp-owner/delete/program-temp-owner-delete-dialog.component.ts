import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramTempOwner } from '../program-temp-owner.model';
import { ProgramTempOwnerService } from '../service/program-temp-owner.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-temp-owner-delete-dialog.component.html',
})
export class ProgramTempOwnerDeleteDialogComponent {
  programTempOwner?: IProgramTempOwner;

  constructor(protected programTempOwnerService: ProgramTempOwnerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programTempOwnerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
