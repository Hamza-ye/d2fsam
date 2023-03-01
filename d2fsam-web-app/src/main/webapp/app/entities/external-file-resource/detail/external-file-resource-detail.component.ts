import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExternalFileResource } from '../external-file-resource.model';

@Component({
  selector: 'app-external-file-resource-detail',
  templateUrl: './external-file-resource-detail.component.html',
})
export class ExternalFileResourceDetailComponent implements OnInit {
  externalFileResource: IExternalFileResource | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ externalFileResource }) => {
      this.externalFileResource = externalFileResource;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
