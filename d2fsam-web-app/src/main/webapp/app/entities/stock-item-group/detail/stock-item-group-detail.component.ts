import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStockItemGroup } from '../stock-item-group.model';

@Component({
  selector: 'app-stock-item-group-detail',
  templateUrl: './stock-item-group-detail.component.html',
})
export class StockItemGroupDetailComponent implements OnInit {
  stockItemGroup: IStockItemGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockItemGroup }) => {
      this.stockItemGroup = stockItemGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
