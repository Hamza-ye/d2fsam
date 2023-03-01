import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDataValue } from '../data-value.model';

@Component({
  selector: 'app-data-value-detail',
  templateUrl: './data-value-detail.component.html',
})
export class DataValueDetailComponent implements OnInit {
  dataValue: IDataValue | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dataValue }) => {
      this.dataValue = dataValue;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
