import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramStageDataElement } from '../program-stage-data-element.model';
import { ProgramStageDataElementService } from '../service/program-stage-data-element.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-stage-data-element-delete-dialog.component.html',
})
export class ProgramStageDataElementDeleteDialogComponent {
  programStageDataElement?: IProgramStageDataElement;

  constructor(protected programStageDataElementService: ProgramStageDataElementService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programStageDataElementService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
