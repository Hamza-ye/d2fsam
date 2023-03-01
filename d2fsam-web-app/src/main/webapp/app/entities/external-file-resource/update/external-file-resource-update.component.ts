import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ExternalFileResourceFormService, ExternalFileResourceFormGroup } from './external-file-resource-form.service';
import { IExternalFileResource } from '../external-file-resource.model';
import { ExternalFileResourceService } from '../service/external-file-resource.service';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { FileResourceService } from 'app/entities/file-resource/service/file-resource.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-external-file-resource-update',
  templateUrl: './external-file-resource-update.component.html',
})
export class ExternalFileResourceUpdateComponent implements OnInit {
  isSaving = false;
  externalFileResource: IExternalFileResource | null = null;

  fileResourcesSharedCollection: IFileResource[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ExternalFileResourceFormGroup = this.externalFileResourceFormService.createExternalFileResourceFormGroup();

  constructor(
    protected externalFileResourceService: ExternalFileResourceService,
    protected externalFileResourceFormService: ExternalFileResourceFormService,
    protected fileResourceService: FileResourceService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFileResource = (o1: IFileResource | null, o2: IFileResource | null): boolean =>
    this.fileResourceService.compareFileResource(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ externalFileResource }) => {
      this.externalFileResource = externalFileResource;
      if (externalFileResource) {
        this.updateForm(externalFileResource);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const externalFileResource = this.externalFileResourceFormService.getExternalFileResource(this.editForm);
    if (externalFileResource.id !== null) {
      this.subscribeToSaveResponse(this.externalFileResourceService.update(externalFileResource));
    } else {
      this.subscribeToSaveResponse(this.externalFileResourceService.create(externalFileResource));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExternalFileResource>>): void {
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

  protected updateForm(externalFileResource: IExternalFileResource): void {
    this.externalFileResource = externalFileResource;
    this.externalFileResourceFormService.resetForm(this.editForm, externalFileResource);

    this.fileResourcesSharedCollection = this.fileResourceService.addFileResourceToCollectionIfMissing<IFileResource>(
      this.fileResourcesSharedCollection,
      externalFileResource.fileResource
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      externalFileResource.createdBy,
      externalFileResource.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.fileResourceService
      .query()
      .pipe(map((res: HttpResponse<IFileResource[]>) => res.body ?? []))
      .pipe(
        map((fileResources: IFileResource[]) =>
          this.fileResourceService.addFileResourceToCollectionIfMissing<IFileResource>(
            fileResources,
            this.externalFileResource?.fileResource
          )
        )
      )
      .subscribe((fileResources: IFileResource[]) => (this.fileResourcesSharedCollection = fileResources));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.externalFileResource?.createdBy,
            this.externalFileResource?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
