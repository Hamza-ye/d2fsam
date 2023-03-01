import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramOwnershipHistory } from '../program-ownership-history.model';

@Component({
  selector: 'app-program-ownership-history-detail',
  templateUrl: './program-ownership-history-detail.component.html',
})
export class ProgramOwnershipHistoryDetailComponent implements OnInit {
  programOwnershipHistory: IProgramOwnershipHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programOwnershipHistory }) => {
      this.programOwnershipHistory = programOwnershipHistory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
