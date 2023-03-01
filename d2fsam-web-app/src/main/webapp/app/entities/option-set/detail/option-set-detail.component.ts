import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOptionSet } from '../option-set.model';

@Component({
  selector: 'app-option-set-detail',
  templateUrl: './option-set-detail.component.html',
})
export class OptionSetDetailComponent implements OnInit {
  optionSet: IOptionSet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ optionSet }) => {
      this.optionSet = optionSet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
