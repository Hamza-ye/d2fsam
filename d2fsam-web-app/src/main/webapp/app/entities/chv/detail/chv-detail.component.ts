import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChv } from '../chv.model';

@Component({
  selector: 'app-chv-detail',
  templateUrl: './chv-detail.component.html',
})
export class ChvDetailComponent implements OnInit {
  chv: IChv | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ chv }) => {
      this.chv = chv;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
