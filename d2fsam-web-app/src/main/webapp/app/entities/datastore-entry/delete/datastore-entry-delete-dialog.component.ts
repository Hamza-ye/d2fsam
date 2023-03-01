import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDatastoreEntry } from '../datastore-entry.model';
import { DatastoreEntryService } from '../service/datastore-entry.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './datastore-entry-delete-dialog.component.html',
})
export class DatastoreEntryDeleteDialogComponent {
  datastoreEntry?: IDatastoreEntry;

  constructor(protected datastoreEntryService: DatastoreEntryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.datastoreEntryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
