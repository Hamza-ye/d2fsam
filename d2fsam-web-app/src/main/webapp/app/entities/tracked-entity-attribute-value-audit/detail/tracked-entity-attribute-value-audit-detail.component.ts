import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';

@Component({
  selector: 'app-tracked-entity-attribute-value-audit-detail',
  templateUrl: './tracked-entity-attribute-value-audit-detail.component.html',
})
export class TrackedEntityAttributeValueAuditDetailComponent implements OnInit {
  trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityAttributeValueAudit }) => {
      this.trackedEntityAttributeValueAudit = trackedEntityAttributeValueAudit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
