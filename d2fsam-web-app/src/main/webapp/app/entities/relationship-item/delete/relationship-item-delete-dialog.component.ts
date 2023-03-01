import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRelationshipItem } from '../relationship-item.model';
import { RelationshipItemService } from '../service/relationship-item.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './relationship-item-delete-dialog.component.html',
})
export class RelationshipItemDeleteDialogComponent {
  relationshipItem?: IRelationshipItem;

  constructor(protected relationshipItemService: RelationshipItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.relationshipItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
