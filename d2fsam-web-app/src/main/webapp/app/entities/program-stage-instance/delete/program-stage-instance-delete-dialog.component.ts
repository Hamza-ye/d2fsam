import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramStageInstance } from '../program-stage-instance.model';
import { ProgramStageInstanceService } from '../service/program-stage-instance.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-stage-instance-delete-dialog.component.html',
})
export class ProgramStageInstanceDeleteDialogComponent {
  programStageInstance?: IProgramStageInstance;

  constructor(protected programStageInstanceService: ProgramStageInstanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programStageInstanceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
