import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';

@Component({
  selector: 'app-tracked-entity-data-value-audit-detail',
  templateUrl: './tracked-entity-data-value-audit-detail.component.html',
})
export class TrackedEntityDataValueAuditDetailComponent implements OnInit {
  trackedEntityDataValueAudit: ITrackedEntityDataValueAudit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityDataValueAudit }) => {
      this.trackedEntityDataValueAudit = trackedEntityDataValueAudit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
