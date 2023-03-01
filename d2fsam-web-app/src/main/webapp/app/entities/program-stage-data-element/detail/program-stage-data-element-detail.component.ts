import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramStageDataElement } from '../program-stage-data-element.model';

@Component({
  selector: 'app-program-stage-data-element-detail',
  templateUrl: './program-stage-data-element-detail.component.html',
})
export class ProgramStageDataElementDetailComponent implements OnInit {
  programStageDataElement: IProgramStageDataElement | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStageDataElement }) => {
      this.programStageDataElement = programStageDataElement;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
