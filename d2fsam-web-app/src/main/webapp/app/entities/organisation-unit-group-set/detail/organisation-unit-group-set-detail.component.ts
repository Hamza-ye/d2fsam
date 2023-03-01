import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrganisationUnitGroupSet } from '../organisation-unit-group-set.model';

@Component({
  selector: 'app-organisation-unit-group-set-detail',
  templateUrl: './organisation-unit-group-set-detail.component.html',
})
export class OrganisationUnitGroupSetDetailComponent implements OnInit {
  organisationUnitGroupSet: IOrganisationUnitGroupSet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnitGroupSet }) => {
      this.organisationUnitGroupSet = organisationUnitGroupSet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
