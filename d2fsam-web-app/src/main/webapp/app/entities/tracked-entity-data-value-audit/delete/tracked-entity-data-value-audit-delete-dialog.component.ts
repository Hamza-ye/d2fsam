import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';
import { TrackedEntityDataValueAuditService } from '../service/tracked-entity-data-value-audit.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-data-value-audit-delete-dialog.component.html',
})
export class TrackedEntityDataValueAuditDeleteDialogComponent {
  trackedEntityDataValueAudit?: ITrackedEntityDataValueAudit;

  constructor(protected trackedEntityDataValueAuditService: TrackedEntityDataValueAuditService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityDataValueAuditService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
