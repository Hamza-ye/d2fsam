import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDatastoreEntry } from '../datastore-entry.model';

@Component({
  selector: 'app-datastore-entry-detail',
  templateUrl: './datastore-entry-detail.component.html',
})
export class DatastoreEntryDetailComponent implements OnInit {
  datastoreEntry: IDatastoreEntry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ datastoreEntry }) => {
      this.datastoreEntry = datastoreEntry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
