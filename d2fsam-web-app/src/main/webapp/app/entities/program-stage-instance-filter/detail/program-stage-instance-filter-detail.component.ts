import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';

@Component({
  selector: 'app-program-stage-instance-filter-detail',
  templateUrl: './program-stage-instance-filter-detail.component.html',
})
export class ProgramStageInstanceFilterDetailComponent implements OnInit {
  programStageInstanceFilter: IProgramStageInstanceFilter | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStageInstanceFilter }) => {
      this.programStageInstanceFilter = programStageInstanceFilter;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
