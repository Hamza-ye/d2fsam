import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMalariaCase } from '../malaria-case.model';

@Component({
  selector: 'app-malaria-case-detail',
  templateUrl: './malaria-case-detail.component.html',
})
export class MalariaCaseDetailComponent implements OnInit {
  malariaCase: IMalariaCase | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ malariaCase }) => {
      this.malariaCase = malariaCase;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
