import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFileResource } from '../file-resource.model';

@Component({
  selector: 'app-file-resource-detail',
  templateUrl: './file-resource-detail.component.html',
})
export class FileResourceDetailComponent implements OnInit {
  fileResource: IFileResource | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fileResource }) => {
      this.fileResource = fileResource;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
