import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISqlView } from '../sql-view.model';

@Component({
  selector: 'app-sql-view-detail',
  templateUrl: './sql-view-detail.component.html',
})
export class SqlViewDetailComponent implements OnInit {
  sqlView: ISqlView | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sqlView }) => {
      this.sqlView = sqlView;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
