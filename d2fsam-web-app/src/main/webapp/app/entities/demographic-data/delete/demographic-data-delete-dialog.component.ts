import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDemographicData } from '../demographic-data.model';
import { DemographicDataService } from '../service/demographic-data.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './demographic-data-delete-dialog.component.html',
})
export class DemographicDataDeleteDialogComponent {
  demographicData?: IDemographicData;

  constructor(protected demographicDataService: DemographicDataService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.demographicDataService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
