import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDataElement } from '../data-element.model';

@Component({
  selector: 'app-data-element-detail',
  templateUrl: './data-element-detail.component.html',
})
export class DataElementDetailComponent implements OnInit {
  dataElement: IDataElement | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dataElement }) => {
      this.dataElement = dataElement;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
