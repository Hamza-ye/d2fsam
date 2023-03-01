import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RelationshipFormService, RelationshipFormGroup } from './relationship-form.service';
import { IRelationship } from '../relationship.model';
import { RelationshipService } from '../service/relationship.service';
import { IRelationshipType } from 'app/entities/relationship-type/relationship-type.model';
import { RelationshipTypeService } from 'app/entities/relationship-type/service/relationship-type.service';
import { IRelationshipItem } from 'app/entities/relationship-item/relationship-item.model';
import { RelationshipItemService } from 'app/entities/relationship-item/service/relationship-item.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-relationship-update',
  templateUrl: './relationship-update.component.html',
})
export class RelationshipUpdateComponent implements OnInit {
  isSaving = false;
  relationship: IRelationship | null = null;

  relationshipTypesSharedCollection: IRelationshipType[] = [];
  relationshipItemsSharedCollection: IRelationshipItem[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: RelationshipFormGroup = this.relationshipFormService.createRelationshipFormGroup();

  constructor(
    protected relationshipService: RelationshipService,
    protected relationshipFormService: RelationshipFormService,
    protected relationshipTypeService: RelationshipTypeService,
    protected relationshipItemService: RelationshipItemService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareRelationshipType = (o1: IRelationshipType | null, o2: IRelationshipType | null): boolean =>
    this.relationshipTypeService.compareRelationshipType(o1, o2);

  compareRelationshipItem = (o1: IRelationshipItem | null, o2: IRelationshipItem | null): boolean =>
    this.relationshipItemService.compareRelationshipItem(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationship }) => {
      this.relationship = relationship;
      if (relationship) {
        this.updateForm(relationship);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relationship = this.relationshipFormService.getRelationship(this.editForm);
    if (relationship.id !== null) {
      this.subscribeToSaveResponse(this.relationshipService.update(relationship));
    } else {
      this.subscribeToSaveResponse(this.relationshipService.create(relationship));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelationship>>): void {
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

  protected updateForm(relationship: IRelationship): void {
    this.relationship = relationship;
    this.relationshipFormService.resetForm(this.editForm, relationship);

    this.relationshipTypesSharedCollection = this.relationshipTypeService.addRelationshipTypeToCollectionIfMissing<IRelationshipType>(
      this.relationshipTypesSharedCollection,
      relationship.relationshipType
    );
    this.relationshipItemsSharedCollection = this.relationshipItemService.addRelationshipItemToCollectionIfMissing<IRelationshipItem>(
      this.relationshipItemsSharedCollection,
      relationship.from,
      relationship.to
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, relationship.updatedBy);
  }

  protected loadRelationshipsOptions(): void {
    this.relationshipTypeService
      .query()
      .pipe(map((res: HttpResponse<IRelationshipType[]>) => res.body ?? []))
      .pipe(
        map((relationshipTypes: IRelationshipType[]) =>
          this.relationshipTypeService.addRelationshipTypeToCollectionIfMissing<IRelationshipType>(
            relationshipTypes,
            this.relationship?.relationshipType
          )
        )
      )
      .subscribe((relationshipTypes: IRelationshipType[]) => (this.relationshipTypesSharedCollection = relationshipTypes));

    this.relationshipItemService
      .query()
      .pipe(map((res: HttpResponse<IRelationshipItem[]>) => res.body ?? []))
      .pipe(
        map((relationshipItems: IRelationshipItem[]) =>
          this.relationshipItemService.addRelationshipItemToCollectionIfMissing<IRelationshipItem>(
            relationshipItems,
            this.relationship?.from,
            this.relationship?.to
          )
        )
      )
      .subscribe((relationshipItems: IRelationshipItem[]) => (this.relationshipItemsSharedCollection = relationshipItems));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.relationship?.updatedBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
