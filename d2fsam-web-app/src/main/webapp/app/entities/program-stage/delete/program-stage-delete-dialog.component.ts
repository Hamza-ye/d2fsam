import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramStage } from '../program-stage.model';
import { ProgramStageService } from '../service/program-stage.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-stage-delete-dialog.component.html',
})
export class ProgramStageDeleteDialogComponent {
  programStage?: IProgramStage;

  constructor(protected programStageService: ProgramStageService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programStageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
