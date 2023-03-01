import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOption } from '../option.model';

@Component({
  selector: 'app-option-detail',
  templateUrl: './option-detail.component.html',
})
export class OptionDetailComponent implements OnInit {
  option: IOption | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ option }) => {
      this.option = option;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
