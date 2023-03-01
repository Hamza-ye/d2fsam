import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRelationshipItem } from '../relationship-item.model';

@Component({
  selector: 'app-relationship-item-detail',
  templateUrl: './relationship-item-detail.component.html',
})
export class RelationshipItemDetailComponent implements OnInit {
  relationshipItem: IRelationshipItem | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationshipItem }) => {
      this.relationshipItem = relationshipItem;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
