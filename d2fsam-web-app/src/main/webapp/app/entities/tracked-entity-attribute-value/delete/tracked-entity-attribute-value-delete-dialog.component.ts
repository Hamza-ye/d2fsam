import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';
import { TrackedEntityAttributeValueService } from '../service/tracked-entity-attribute-value.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-attribute-value-delete-dialog.component.html',
})
export class TrackedEntityAttributeValueDeleteDialogComponent {
  trackedEntityAttributeValue?: ITrackedEntityAttributeValue;

  constructor(protected trackedEntityAttributeValueService: TrackedEntityAttributeValueService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityAttributeValueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
