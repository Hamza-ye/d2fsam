import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMetadataVersion } from '../metadata-version.model';
import { MetadataVersionService } from '../service/metadata-version.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './metadata-version-delete-dialog.component.html',
})
export class MetadataVersionDeleteDialogComponent {
  metadataVersion?: IMetadataVersion;

  constructor(protected metadataVersionService: MetadataVersionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.metadataVersionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
