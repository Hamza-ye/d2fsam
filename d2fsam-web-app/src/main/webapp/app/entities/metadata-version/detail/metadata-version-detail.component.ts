import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMetadataVersion } from '../metadata-version.model';

@Component({
  selector: 'app-metadata-version-detail',
  templateUrl: './metadata-version-detail.component.html',
})
export class MetadataVersionDetailComponent implements OnInit {
  metadataVersion: IMetadataVersion | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataVersion }) => {
      this.metadataVersion = metadataVersion;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
