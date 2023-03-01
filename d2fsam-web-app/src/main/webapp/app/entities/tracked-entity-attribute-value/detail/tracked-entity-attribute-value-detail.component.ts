import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';

@Component({
  selector: 'app-tracked-entity-attribute-value-detail',
  templateUrl: './tracked-entity-attribute-value-detail.component.html',
})
export class TrackedEntityAttributeValueDetailComponent implements OnInit {
  trackedEntityAttributeValue: ITrackedEntityAttributeValue | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityAttributeValue }) => {
      this.trackedEntityAttributeValue = trackedEntityAttributeValue;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
