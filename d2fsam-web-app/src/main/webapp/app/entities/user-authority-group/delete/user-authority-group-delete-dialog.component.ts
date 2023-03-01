import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserAuthorityGroup } from '../user-authority-group.model';
import { UserAuthorityGroupService } from '../service/user-authority-group.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './user-authority-group-delete-dialog.component.html',
})
export class UserAuthorityGroupDeleteDialogComponent {
  userAuthorityGroup?: IUserAuthorityGroup;

  constructor(protected userAuthorityGroupService: UserAuthorityGroupService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userAuthorityGroupService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
