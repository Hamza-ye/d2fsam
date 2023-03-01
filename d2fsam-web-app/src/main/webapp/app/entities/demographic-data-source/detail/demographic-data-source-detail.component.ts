import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDemographicDataSource } from '../demographic-data-source.model';

@Component({
  selector: 'app-demographic-data-source-detail',
  templateUrl: './demographic-data-source-detail.component.html',
})
export class DemographicDataSourceDetailComponent implements OnInit {
  demographicDataSource: IDemographicDataSource | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demographicDataSource }) => {
      this.demographicDataSource = demographicDataSource;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
