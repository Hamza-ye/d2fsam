import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramInstance } from '../program-instance.model';

@Component({
  selector: 'app-program-instance-detail',
  templateUrl: './program-instance-detail.component.html',
})
export class ProgramInstanceDetailComponent implements OnInit {
  programInstance: IProgramInstance | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programInstance }) => {
      this.programInstance = programInstance;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
