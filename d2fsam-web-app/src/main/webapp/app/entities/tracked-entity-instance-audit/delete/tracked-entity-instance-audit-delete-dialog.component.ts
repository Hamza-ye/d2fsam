import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';
import { TrackedEntityInstanceAuditService } from '../service/tracked-entity-instance-audit.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-instance-audit-delete-dialog.component.html',
})
export class TrackedEntityInstanceAuditDeleteDialogComponent {
  trackedEntityInstanceAudit?: ITrackedEntityInstanceAudit;

  constructor(protected trackedEntityInstanceAuditService: TrackedEntityInstanceAuditService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityInstanceAuditService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
