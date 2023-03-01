import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';
import { ProgramStageInstanceFilterService } from '../service/program-stage-instance-filter.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-stage-instance-filter-delete-dialog.component.html',
})
export class ProgramStageInstanceFilterDeleteDialogComponent {
  programStageInstanceFilter?: IProgramStageInstanceFilter;

  constructor(protected programStageInstanceFilterService: ProgramStageInstanceFilterService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programStageInstanceFilterService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
