import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityInstance } from '../tracked-entity-instance.model';
import { TrackedEntityInstanceService } from '../service/tracked-entity-instance.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-instance-delete-dialog.component.html',
})
export class TrackedEntityInstanceDeleteDialogComponent {
  trackedEntityInstance?: ITrackedEntityInstance;

  constructor(protected trackedEntityInstanceService: TrackedEntityInstanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityInstanceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
