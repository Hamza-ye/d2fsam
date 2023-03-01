import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';

@Component({
  selector: 'app-tracked-entity-program-owner-detail',
  templateUrl: './tracked-entity-program-owner-detail.component.html',
})
export class TrackedEntityProgramOwnerDetailComponent implements OnInit {
  trackedEntityProgramOwner: ITrackedEntityProgramOwner | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityProgramOwner }) => {
      this.trackedEntityProgramOwner = trackedEntityProgramOwner;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
