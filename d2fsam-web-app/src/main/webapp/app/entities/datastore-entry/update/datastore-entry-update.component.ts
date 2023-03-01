import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DatastoreEntryFormService, DatastoreEntryFormGroup } from './datastore-entry-form.service';
import { IDatastoreEntry } from '../datastore-entry.model';
import { DatastoreEntryService } from '../service/datastore-entry.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-datastore-entry-update',
  templateUrl: './datastore-entry-update.component.html',
})
export class DatastoreEntryUpdateComponent implements OnInit {
  isSaving = false;
  datastoreEntry: IDatastoreEntry | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: DatastoreEntryFormGroup = this.datastoreEntryFormService.createDatastoreEntryFormGroup();

  constructor(
    protected datastoreEntryService: DatastoreEntryService,
    protected datastoreEntryFormService: DatastoreEntryFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ datastoreEntry }) => {
      this.datastoreEntry = datastoreEntry;
      if (datastoreEntry) {
        this.updateForm(datastoreEntry);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const datastoreEntry = this.datastoreEntryFormService.getDatastoreEntry(this.editForm);
    if (datastoreEntry.id !== null) {
      this.subscribeToSaveResponse(this.datastoreEntryService.update(datastoreEntry));
    } else {
      this.subscribeToSaveResponse(this.datastoreEntryService.create(datastoreEntry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDatastoreEntry>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(datastoreEntry: IDatastoreEntry): void {
    this.datastoreEntry = datastoreEntry;
    this.datastoreEntryFormService.resetForm(this.editForm, datastoreEntry);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      datastoreEntry.createdBy,
      datastoreEntry.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.datastoreEntry?.createdBy, this.datastoreEntry?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
