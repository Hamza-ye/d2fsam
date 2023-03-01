import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRelationshipConstraint } from '../relationship-constraint.model';
import { RelationshipConstraintService } from '../service/relationship-constraint.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './relationship-constraint-delete-dialog.component.html',
})
export class RelationshipConstraintDeleteDialogComponent {
  relationshipConstraint?: IRelationshipConstraint;

  constructor(protected relationshipConstraintService: RelationshipConstraintService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.relationshipConstraintService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
