import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';

@Component({
  selector: 'app-program-tracked-entity-attribute-detail',
  templateUrl: './program-tracked-entity-attribute-detail.component.html',
})
export class ProgramTrackedEntityAttributeDetailComponent implements OnInit {
  programTrackedEntityAttribute: IProgramTrackedEntityAttribute | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programTrackedEntityAttribute }) => {
      this.programTrackedEntityAttribute = programTrackedEntityAttribute;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
