import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrganisationUnitLevel } from '../organisation-unit-level.model';

@Component({
  selector: 'app-organisation-unit-level-detail',
  templateUrl: './organisation-unit-level-detail.component.html',
})
export class OrganisationUnitLevelDetailComponent implements OnInit {
  organisationUnitLevel: IOrganisationUnitLevel | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnitLevel }) => {
      this.organisationUnitLevel = organisationUnitLevel;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
