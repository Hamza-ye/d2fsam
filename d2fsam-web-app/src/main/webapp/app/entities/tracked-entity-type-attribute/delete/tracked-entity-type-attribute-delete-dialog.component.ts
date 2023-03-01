import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';
import { TrackedEntityTypeAttributeService } from '../service/tracked-entity-type-attribute.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-type-attribute-delete-dialog.component.html',
})
export class TrackedEntityTypeAttributeDeleteDialogComponent {
  trackedEntityTypeAttribute?: ITrackedEntityTypeAttribute;

  constructor(protected trackedEntityTypeAttributeService: TrackedEntityTypeAttributeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityTypeAttributeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
