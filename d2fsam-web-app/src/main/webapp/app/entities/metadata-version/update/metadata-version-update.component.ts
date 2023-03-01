import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MetadataVersionFormService, MetadataVersionFormGroup } from './metadata-version-form.service';
import { IMetadataVersion } from '../metadata-version.model';
import { MetadataVersionService } from '../service/metadata-version.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { VersionType } from 'app/entities/enumerations/version-type.model';

@Component({
  selector: 'app-metadata-version-update',
  templateUrl: './metadata-version-update.component.html',
})
export class MetadataVersionUpdateComponent implements OnInit {
  isSaving = false;
  metadataVersion: IMetadataVersion | null = null;
  versionTypeValues = Object.keys(VersionType);

  usersSharedCollection: IUser[] = [];

  editForm: MetadataVersionFormGroup = this.metadataVersionFormService.createMetadataVersionFormGroup();

  constructor(
    protected metadataVersionService: MetadataVersionService,
    protected metadataVersionFormService: MetadataVersionFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ metadataVersion }) => {
      this.metadataVersion = metadataVersion;
      if (metadataVersion) {
        this.updateForm(metadataVersion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const metadataVersion = this.metadataVersionFormService.getMetadataVersion(this.editForm);
    if (metadataVersion.id !== null) {
      this.subscribeToSaveResponse(this.metadataVersionService.update(metadataVersion));
    } else {
      this.subscribeToSaveResponse(this.metadataVersionService.create(metadataVersion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMetadataVersion>>): void {
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

  protected updateForm(metadataVersion: IMetadataVersion): void {
    this.metadataVersion = metadataVersion;
    this.metadataVersionFormService.resetForm(this.editForm, metadataVersion);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      metadataVersion.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.metadataVersion?.updatedBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
