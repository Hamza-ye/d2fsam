import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';
import { TrackedEntityProgramOwnerService } from '../service/tracked-entity-program-owner.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-program-owner-delete-dialog.component.html',
})
export class TrackedEntityProgramOwnerDeleteDialogComponent {
  trackedEntityProgramOwner?: ITrackedEntityProgramOwner;

  constructor(protected trackedEntityProgramOwnerService: TrackedEntityProgramOwnerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityProgramOwnerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
