import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDataInputPeriod } from '../data-input-period.model';

@Component({
  selector: 'app-data-input-period-detail',
  templateUrl: './data-input-period-detail.component.html',
})
export class DataInputPeriodDetailComponent implements OnInit {
  dataInputPeriod: IDataInputPeriod | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dataInputPeriod }) => {
      this.dataInputPeriod = dataInputPeriod;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
