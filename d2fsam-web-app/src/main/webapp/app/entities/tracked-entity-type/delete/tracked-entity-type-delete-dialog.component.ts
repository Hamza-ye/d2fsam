import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityType } from '../tracked-entity-type.model';
import { TrackedEntityTypeService } from '../service/tracked-entity-type.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-type-delete-dialog.component.html',
})
export class TrackedEntityTypeDeleteDialogComponent {
  trackedEntityType?: ITrackedEntityType;

  constructor(protected trackedEntityTypeService: TrackedEntityTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
