import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISqlView } from '../sql-view.model';
import { SqlViewService } from '../service/sql-view.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './sql-view-delete-dialog.component.html',
})
export class SqlViewDeleteDialogComponent {
  sqlView?: ISqlView;

  constructor(protected sqlViewService: SqlViewService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sqlViewService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
