import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FileResourceFormService, FileResourceFormGroup } from './file-resource-form.service';
import { IFileResource } from '../file-resource.model';
import { FileResourceService } from '../service/file-resource.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { FileResourceDomain } from 'app/entities/enumerations/file-resource-domain.model';

@Component({
  selector: 'app-file-resource-update',
  templateUrl: './file-resource-update.component.html',
})
export class FileResourceUpdateComponent implements OnInit {
  isSaving = false;
  fileResource: IFileResource | null = null;
  fileResourceDomainValues = Object.keys(FileResourceDomain);

  usersSharedCollection: IUser[] = [];

  editForm: FileResourceFormGroup = this.fileResourceFormService.createFileResourceFormGroup();

  constructor(
    protected fileResourceService: FileResourceService,
    protected fileResourceFormService: FileResourceFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fileResource }) => {
      this.fileResource = fileResource;
      if (fileResource) {
        this.updateForm(fileResource);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fileResource = this.fileResourceFormService.getFileResource(this.editForm);
    if (fileResource.id !== null) {
      this.subscribeToSaveResponse(this.fileResourceService.update(fileResource));
    } else {
      this.subscribeToSaveResponse(this.fileResourceService.create(fileResource));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFileResource>>): void {
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

  protected updateForm(fileResource: IFileResource): void {
    this.fileResource = fileResource;
    this.fileResourceFormService.resetForm(this.editForm, fileResource);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      fileResource.createdBy,
      fileResource.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.fileResource?.createdBy, this.fileResource?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
