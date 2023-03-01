import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgram } from '../program.model';

@Component({
  selector: 'app-program-detail',
  templateUrl: './program-detail.component.html',
})
export class ProgramDetailComponent implements OnInit {
  program: IProgram | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ program }) => {
      this.program = program;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
