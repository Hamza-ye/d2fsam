import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DocumentFormService, DocumentFormGroup } from './document-form.service';
import { IDocument } from '../document.model';
import { DocumentService } from '../service/document.service';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { FileResourceService } from 'app/entities/file-resource/service/file-resource.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-document-update',
  templateUrl: './document-update.component.html',
})
export class DocumentUpdateComponent implements OnInit {
  isSaving = false;
  document: IDocument | null = null;

  fileResourcesSharedCollection: IFileResource[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: DocumentFormGroup = this.documentFormService.createDocumentFormGroup();

  constructor(
    protected documentService: DocumentService,
    protected documentFormService: DocumentFormService,
    protected fileResourceService: FileResourceService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFileResource = (o1: IFileResource | null, o2: IFileResource | null): boolean =>
    this.fileResourceService.compareFileResource(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ document }) => {
      this.document = document;
      if (document) {
        this.updateForm(document);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const document = this.documentFormService.getDocument(this.editForm);
    if (document.id !== null) {
      this.subscribeToSaveResponse(this.documentService.update(document));
    } else {
      this.subscribeToSaveResponse(this.documentService.create(document));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDocument>>): void {
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

  protected updateForm(document: IDocument): void {
    this.document = document;
    this.documentFormService.resetForm(this.editForm, document);

    this.fileResourcesSharedCollection = this.fileResourceService.addFileResourceToCollectionIfMissing<IFileResource>(
      this.fileResourcesSharedCollection,
      document.fileResource
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      document.createdBy,
      document.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.fileResourceService
      .query()
      .pipe(map((res: HttpResponse<IFileResource[]>) => res.body ?? []))
      .pipe(
        map((fileResources: IFileResource[]) =>
          this.fileResourceService.addFileResourceToCollectionIfMissing<IFileResource>(fileResources, this.document?.fileResource)
        )
      )
      .subscribe((fileResources: IFileResource[]) => (this.fileResourcesSharedCollection = fileResources));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.document?.createdBy, this.document?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
