import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';
import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-attribute-value-audit-delete-dialog.component.html',
})
export class TrackedEntityAttributeValueAuditDeleteDialogComponent {
  trackedEntityAttributeValueAudit?: ITrackedEntityAttributeValueAudit;

  constructor(
    protected trackedEntityAttributeValueAuditService: TrackedEntityAttributeValueAuditService,
    protected activeModal: NgbActiveModal
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityAttributeValueAuditService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
