import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramStageInstance } from '../program-stage-instance.model';

@Component({
  selector: 'app-program-stage-instance-detail',
  templateUrl: './program-stage-instance-detail.component.html',
})
export class ProgramStageInstanceDetailComponent implements OnInit {
  programStageInstance: IProgramStageInstance | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStageInstance }) => {
      this.programStageInstance = programStageInstance;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
