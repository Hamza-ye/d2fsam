import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRelativePeriods } from '../relative-periods.model';

@Component({
  selector: 'app-relative-periods-detail',
  templateUrl: './relative-periods-detail.component.html',
})
export class RelativePeriodsDetailComponent implements OnInit {
  relativePeriods: IRelativePeriods | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relativePeriods }) => {
      this.relativePeriods = relativePeriods;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
