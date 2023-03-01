import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrganisationUnit } from '../organisation-unit.model';

@Component({
  selector: 'app-organisation-unit-detail',
  templateUrl: './organisation-unit-detail.component.html',
})
export class OrganisationUnitDetailComponent implements OnInit {
  organisationUnit: IOrganisationUnit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnit }) => {
      this.organisationUnit = organisationUnit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
