import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';

@Component({
  selector: 'app-tracked-entity-instance-audit-detail',
  templateUrl: './tracked-entity-instance-audit-detail.component.html',
})
export class TrackedEntityInstanceAuditDetailComponent implements OnInit {
  trackedEntityInstanceAudit: ITrackedEntityInstanceAudit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityInstanceAudit }) => {
      this.trackedEntityInstanceAudit = trackedEntityInstanceAudit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
