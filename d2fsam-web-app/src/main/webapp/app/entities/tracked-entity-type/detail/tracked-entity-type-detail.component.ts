import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityType } from '../tracked-entity-type.model';

@Component({
  selector: 'app-tracked-entity-type-detail',
  templateUrl: './tracked-entity-type-detail.component.html',
})
export class TrackedEntityTypeDetailComponent implements OnInit {
  trackedEntityType: ITrackedEntityType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityType }) => {
      this.trackedEntityType = trackedEntityType;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
