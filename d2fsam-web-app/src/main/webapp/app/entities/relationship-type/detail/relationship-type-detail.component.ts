import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRelationshipType } from '../relationship-type.model';

@Component({
  selector: 'app-relationship-type-detail',
  templateUrl: './relationship-type-detail.component.html',
})
export class RelationshipTypeDetailComponent implements OnInit {
  relationshipType: IRelationshipType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationshipType }) => {
      this.relationshipType = relationshipType;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
