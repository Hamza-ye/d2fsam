import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';

@Component({
  selector: 'app-tracked-entity-type-attribute-detail',
  templateUrl: './tracked-entity-type-attribute-detail.component.html',
})
export class TrackedEntityTypeAttributeDetailComponent implements OnInit {
  trackedEntityTypeAttribute: ITrackedEntityTypeAttribute | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityTypeAttribute }) => {
      this.trackedEntityTypeAttribute = trackedEntityTypeAttribute;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
