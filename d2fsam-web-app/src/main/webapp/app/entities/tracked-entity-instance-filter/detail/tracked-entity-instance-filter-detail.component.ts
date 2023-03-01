import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';

@Component({
  selector: 'app-tracked-entity-instance-filter-detail',
  templateUrl: './tracked-entity-instance-filter-detail.component.html',
})
export class TrackedEntityInstanceFilterDetailComponent implements OnInit {
  trackedEntityInstanceFilter: ITrackedEntityInstanceFilter | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityInstanceFilter }) => {
      this.trackedEntityInstanceFilter = trackedEntityInstanceFilter;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
