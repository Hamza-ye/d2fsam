import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRelationshipConstraint } from '../relationship-constraint.model';

@Component({
  selector: 'app-relationship-constraint-detail',
  templateUrl: './relationship-constraint-detail.component.html',
})
export class RelationshipConstraintDetailComponent implements OnInit {
  relationshipConstraint: IRelationshipConstraint | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationshipConstraint }) => {
      this.relationshipConstraint = relationshipConstraint;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
