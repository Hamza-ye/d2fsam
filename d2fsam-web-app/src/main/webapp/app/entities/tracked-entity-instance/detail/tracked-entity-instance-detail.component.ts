import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityInstance } from '../tracked-entity-instance.model';

@Component({
  selector: 'app-tracked-entity-instance-detail',
  templateUrl: './tracked-entity-instance-detail.component.html',
})
export class TrackedEntityInstanceDetailComponent implements OnInit {
  trackedEntityInstance: ITrackedEntityInstance | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityInstance }) => {
      this.trackedEntityInstance = trackedEntityInstance;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
