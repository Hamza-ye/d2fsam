import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramTempOwner } from '../program-temp-owner.model';

@Component({
  selector: 'app-program-temp-owner-detail',
  templateUrl: './program-temp-owner-detail.component.html',
})
export class ProgramTempOwnerDetailComponent implements OnInit {
  programTempOwner: IProgramTempOwner | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programTempOwner }) => {
      this.programTempOwner = programTempOwner;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
