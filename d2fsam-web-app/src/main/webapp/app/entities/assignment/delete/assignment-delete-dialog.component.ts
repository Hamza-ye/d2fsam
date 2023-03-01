import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAssignment } from '../assignment.model';
import { AssignmentService } from '../service/assignment.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './assignment-delete-dialog.component.html',
})
export class AssignmentDeleteDialogComponent {
  assignment?: IAssignment;

  constructor(protected assignmentService: AssignmentService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.assignmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
