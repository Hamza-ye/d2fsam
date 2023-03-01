import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';
import { ProgramTrackedEntityAttributeService } from '../service/program-tracked-entity-attribute.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './program-tracked-entity-attribute-delete-dialog.component.html',
})
export class ProgramTrackedEntityAttributeDeleteDialogComponent {
  programTrackedEntityAttribute?: IProgramTrackedEntityAttribute;

  constructor(
    protected programTrackedEntityAttributeService: ProgramTrackedEntityAttributeService,
    protected activeModal: NgbActiveModal
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.programTrackedEntityAttributeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
