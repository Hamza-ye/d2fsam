import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDemographicData } from '../demographic-data.model';

@Component({
  selector: 'app-demographic-data-detail',
  templateUrl: './demographic-data-detail.component.html',
})
export class DemographicDataDetailComponent implements OnInit {
  demographicData: IDemographicData | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demographicData }) => {
      this.demographicData = demographicData;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
