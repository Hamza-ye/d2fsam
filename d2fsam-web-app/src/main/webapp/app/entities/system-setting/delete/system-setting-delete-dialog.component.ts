import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISystemSetting } from '../system-setting.model';
import { SystemSettingService } from '../service/system-setting.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './system-setting-delete-dialog.component.html',
})
export class SystemSettingDeleteDialogComponent {
  systemSetting?: ISystemSetting;

  constructor(protected systemSettingService: SystemSettingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.systemSettingService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
