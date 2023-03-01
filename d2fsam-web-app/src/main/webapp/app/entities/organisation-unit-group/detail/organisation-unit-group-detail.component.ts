import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrganisationUnitGroup } from '../organisation-unit-group.model';

@Component({
  selector: 'app-organisation-unit-group-detail',
  templateUrl: './organisation-unit-group-detail.component.html',
})
export class OrganisationUnitGroupDetailComponent implements OnInit {
  organisationUnitGroup: IOrganisationUnitGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ organisationUnitGroup }) => {
      this.organisationUnitGroup = organisationUnitGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
