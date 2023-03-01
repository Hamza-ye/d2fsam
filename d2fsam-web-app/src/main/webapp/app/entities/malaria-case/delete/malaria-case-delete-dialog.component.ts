import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMalariaCase } from '../malaria-case.model';
import { MalariaCaseService } from '../service/malaria-case.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './malaria-case-delete-dialog.component.html',
})
export class MalariaCaseDeleteDialogComponent {
  malariaCase?: IMalariaCase;

  constructor(protected malariaCaseService: MalariaCaseService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.malariaCaseService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
