import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramStage } from '../program-stage.model';

@Component({
  selector: 'app-program-stage-detail',
  templateUrl: './program-stage-detail.component.html',
})
export class ProgramStageDetailComponent implements OnInit {
  programStage: IProgramStage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStage }) => {
      this.programStage = programStage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
