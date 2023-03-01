import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import { TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './tracked-entity-instance-filter-delete-dialog.component.html',
})
export class TrackedEntityInstanceFilterDeleteDialogComponent {
  trackedEntityInstanceFilter?: ITrackedEntityInstanceFilter;

  constructor(protected trackedEntityInstanceFilterService: TrackedEntityInstanceFilterService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackedEntityInstanceFilterService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
