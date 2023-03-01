import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOptionSet } from '../option-set.model';
import { OptionSetService } from '../service/option-set.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './option-set-delete-dialog.component.html',
})
export class OptionSetDeleteDialogComponent {
  optionSet?: IOptionSet;

  constructor(protected optionSetService: OptionSetService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.optionSetService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
