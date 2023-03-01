import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TrackedEntityTypeFormService, TrackedEntityTypeFormGroup } from './tracked-entity-type-form.service';
import { ITrackedEntityType } from '../tracked-entity-type.model';
import { TrackedEntityTypeService } from '../service/tracked-entity-type.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

@Component({
  selector: 'app-tracked-entity-type-update',
  templateUrl: './tracked-entity-type-update.component.html',
})
export class TrackedEntityTypeUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityType: ITrackedEntityType | null = null;
  featureTypeValues = Object.keys(FeatureType);

  usersSharedCollection: IUser[] = [];

  editForm: TrackedEntityTypeFormGroup = this.trackedEntityTypeFormService.createTrackedEntityTypeFormGroup();

  constructor(
    protected trackedEntityTypeService: TrackedEntityTypeService,
    protected trackedEntityTypeFormService: TrackedEntityTypeFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityType }) => {
      this.trackedEntityType = trackedEntityType;
      if (trackedEntityType) {
        this.updateForm(trackedEntityType);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityType = this.trackedEntityTypeFormService.getTrackedEntityType(this.editForm);
    if (trackedEntityType.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityTypeService.update(trackedEntityType));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityTypeService.create(trackedEntityType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityType>>): void {
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

  protected updateForm(trackedEntityType: ITrackedEntityType): void {
    this.trackedEntityType = trackedEntityType;
    this.trackedEntityTypeFormService.resetForm(this.editForm, trackedEntityType);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      trackedEntityType.createdBy,
      trackedEntityType.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.trackedEntityType?.createdBy, this.trackedEntityType?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
