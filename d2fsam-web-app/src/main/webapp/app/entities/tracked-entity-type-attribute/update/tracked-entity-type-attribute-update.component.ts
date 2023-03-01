import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TrackedEntityTypeAttributeFormService, TrackedEntityTypeAttributeFormGroup } from './tracked-entity-type-attribute-form.service';
import { ITrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';
import { TrackedEntityTypeAttributeService } from '../service/tracked-entity-type-attribute.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-tracked-entity-type-attribute-update',
  templateUrl: './tracked-entity-type-attribute-update.component.html',
})
export class TrackedEntityTypeAttributeUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityTypeAttribute: ITrackedEntityTypeAttribute | null = null;

  dataElementsSharedCollection: IDataElement[] = [];
  trackedEntityTypesSharedCollection: ITrackedEntityType[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: TrackedEntityTypeAttributeFormGroup = this.trackedEntityTypeAttributeFormService.createTrackedEntityTypeAttributeFormGroup();

  constructor(
    protected trackedEntityTypeAttributeService: TrackedEntityTypeAttributeService,
    protected trackedEntityTypeAttributeFormService: TrackedEntityTypeAttributeFormService,
    protected dataElementService: DataElementService,
    protected trackedEntityTypeService: TrackedEntityTypeService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  compareTrackedEntityType = (o1: ITrackedEntityType | null, o2: ITrackedEntityType | null): boolean =>
    this.trackedEntityTypeService.compareTrackedEntityType(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityTypeAttribute }) => {
      this.trackedEntityTypeAttribute = trackedEntityTypeAttribute;
      if (trackedEntityTypeAttribute) {
        this.updateForm(trackedEntityTypeAttribute);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityTypeAttribute = this.trackedEntityTypeAttributeFormService.getTrackedEntityTypeAttribute(this.editForm);
    if (trackedEntityTypeAttribute.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityTypeAttributeService.update(trackedEntityTypeAttribute));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityTypeAttributeService.create(trackedEntityTypeAttribute));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityTypeAttribute>>): void {
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

  protected updateForm(trackedEntityTypeAttribute: ITrackedEntityTypeAttribute): void {
    this.trackedEntityTypeAttribute = trackedEntityTypeAttribute;
    this.trackedEntityTypeAttributeFormService.resetForm(this.editForm, trackedEntityTypeAttribute);

    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      trackedEntityTypeAttribute.trackedEntityAttribute
    );
    this.trackedEntityTypesSharedCollection = this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
      this.trackedEntityTypesSharedCollection,
      trackedEntityTypeAttribute.trackedEntityType
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      trackedEntityTypeAttribute.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.dataElementService
      .query()
      .pipe(map((res: HttpResponse<IDataElement[]>) => res.body ?? []))
      .pipe(
        map((dataElements: IDataElement[]) =>
          this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
            dataElements,
            this.trackedEntityTypeAttribute?.trackedEntityAttribute
          )
        )
      )
      .subscribe((dataElements: IDataElement[]) => (this.dataElementsSharedCollection = dataElements));

    this.trackedEntityTypeService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityType[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityTypes: ITrackedEntityType[]) =>
          this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
            trackedEntityTypes,
            this.trackedEntityTypeAttribute?.trackedEntityType
          )
        )
      )
      .subscribe((trackedEntityTypes: ITrackedEntityType[]) => (this.trackedEntityTypesSharedCollection = trackedEntityTypes));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.trackedEntityTypeAttribute?.updatedBy))
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
