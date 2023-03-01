import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RelationshipTypeFormService, RelationshipTypeFormGroup } from './relationship-type-form.service';
import { IRelationshipType } from '../relationship-type.model';
import { RelationshipTypeService } from '../service/relationship-type.service';
import { IRelationshipConstraint } from 'app/entities/relationship-constraint/relationship-constraint.model';
import { RelationshipConstraintService } from 'app/entities/relationship-constraint/service/relationship-constraint.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-relationship-type-update',
  templateUrl: './relationship-type-update.component.html',
})
export class RelationshipTypeUpdateComponent implements OnInit {
  isSaving = false;
  relationshipType: IRelationshipType | null = null;

  relationshipConstraintsSharedCollection: IRelationshipConstraint[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: RelationshipTypeFormGroup = this.relationshipTypeFormService.createRelationshipTypeFormGroup();

  constructor(
    protected relationshipTypeService: RelationshipTypeService,
    protected relationshipTypeFormService: RelationshipTypeFormService,
    protected relationshipConstraintService: RelationshipConstraintService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareRelationshipConstraint = (o1: IRelationshipConstraint | null, o2: IRelationshipConstraint | null): boolean =>
    this.relationshipConstraintService.compareRelationshipConstraint(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationshipType }) => {
      this.relationshipType = relationshipType;
      if (relationshipType) {
        this.updateForm(relationshipType);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relationshipType = this.relationshipTypeFormService.getRelationshipType(this.editForm);
    if (relationshipType.id !== null) {
      this.subscribeToSaveResponse(this.relationshipTypeService.update(relationshipType));
    } else {
      this.subscribeToSaveResponse(this.relationshipTypeService.create(relationshipType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelationshipType>>): void {
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

  protected updateForm(relationshipType: IRelationshipType): void {
    this.relationshipType = relationshipType;
    this.relationshipTypeFormService.resetForm(this.editForm, relationshipType);

    this.relationshipConstraintsSharedCollection =
      this.relationshipConstraintService.addRelationshipConstraintToCollectionIfMissing<IRelationshipConstraint>(
        this.relationshipConstraintsSharedCollection,
        relationshipType.fromConstraint,
        relationshipType.toConstraint
      );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      relationshipType.createdBy,
      relationshipType.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.relationshipConstraintService
      .query()
      .pipe(map((res: HttpResponse<IRelationshipConstraint[]>) => res.body ?? []))
      .pipe(
        map((relationshipConstraints: IRelationshipConstraint[]) =>
          this.relationshipConstraintService.addRelationshipConstraintToCollectionIfMissing<IRelationshipConstraint>(
            relationshipConstraints,
            this.relationshipType?.fromConstraint,
            this.relationshipType?.toConstraint
          )
        )
      )
      .subscribe(
        (relationshipConstraints: IRelationshipConstraint[]) => (this.relationshipConstraintsSharedCollection = relationshipConstraints)
      );

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.relationshipType?.createdBy, this.relationshipType?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
