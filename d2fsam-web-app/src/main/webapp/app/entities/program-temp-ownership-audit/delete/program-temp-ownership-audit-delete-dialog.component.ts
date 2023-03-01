import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';
import { ProgramTempOwnershipAuditService } from '../service/program-temp-ownership-audit.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-temp-ownership-audit-delete-dialog.component.html',
})
export class ProgramTempOwnershipAuditDeleteDialogComponent {
  programTempOwnershipAudit?: IProgramTempOwnershipAudit;

  constructor(protected programTempOwnershipAuditService: ProgramTempOwnershipAuditService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programTempOwnershipAuditService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
