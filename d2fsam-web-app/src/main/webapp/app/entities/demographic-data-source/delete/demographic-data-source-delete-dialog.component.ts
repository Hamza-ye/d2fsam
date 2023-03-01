import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDemographicDataSource } from '../demographic-data-source.model';
import { DemographicDataSourceService } from '../service/demographic-data-source.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './demographic-data-source-delete-dialog.component.html',
})
export class DemographicDataSourceDeleteDialogComponent {
  demographicDataSource?: IDemographicDataSource;

  constructor(protected demographicDataSourceService: DemographicDataSourceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.demographicDataSourceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
