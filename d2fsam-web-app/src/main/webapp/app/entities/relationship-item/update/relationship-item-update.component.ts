import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RelationshipItemFormService, RelationshipItemFormGroup } from './relationship-item-form.service';
import { IRelationshipItem } from '../relationship-item.model';
import { RelationshipItemService } from '../service/relationship-item.service';
import { IRelationship } from 'app/entities/relationship/relationship.model';
import { RelationshipService } from 'app/entities/relationship/service/relationship.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { ProgramInstanceService } from 'app/entities/program-instance/service/program-instance.service';
import { IProgramStageInstance } from 'app/entities/program-stage-instance/program-stage-instance.model';
import { ProgramStageInstanceService } from 'app/entities/program-stage-instance/service/program-stage-instance.service';

@Component({
  selector: 'app-relationship-item-update',
  templateUrl: './relationship-item-update.component.html',
})
export class RelationshipItemUpdateComponent implements OnInit {
  isSaving = false;
  relationshipItem: IRelationshipItem | null = null;

  relationshipsSharedCollection: IRelationship[] = [];
  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];
  programInstancesSharedCollection: IProgramInstance[] = [];
  programStageInstancesSharedCollection: IProgramStageInstance[] = [];

  editForm: RelationshipItemFormGroup = this.relationshipItemFormService.createRelationshipItemFormGroup();

  constructor(
    protected relationshipItemService: RelationshipItemService,
    protected relationshipItemFormService: RelationshipItemFormService,
    protected relationshipService: RelationshipService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected programInstanceService: ProgramInstanceService,
    protected programStageInstanceService: ProgramStageInstanceService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareRelationship = (o1: IRelationship | null, o2: IRelationship | null): boolean =>
    this.relationshipService.compareRelationship(o1, o2);

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  compareProgramInstance = (o1: IProgramInstance | null, o2: IProgramInstance | null): boolean =>
    this.programInstanceService.compareProgramInstance(o1, o2);

  compareProgramStageInstance = (o1: IProgramStageInstance | null, o2: IProgramStageInstance | null): boolean =>
    this.programStageInstanceService.compareProgramStageInstance(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationshipItem }) => {
      this.relationshipItem = relationshipItem;
      if (relationshipItem) {
        this.updateForm(relationshipItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relationshipItem = this.relationshipItemFormService.getRelationshipItem(this.editForm);
    if (relationshipItem.id !== null) {
      this.subscribeToSaveResponse(this.relationshipItemService.update(relationshipItem));
    } else {
      this.subscribeToSaveResponse(this.relationshipItemService.create(relationshipItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelationshipItem>>): void {
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

  protected updateForm(relationshipItem: IRelationshipItem): void {
    this.relationshipItem = relationshipItem;
    this.relationshipItemFormService.resetForm(this.editForm, relationshipItem);

    this.relationshipsSharedCollection = this.relationshipService.addRelationshipToCollectionIfMissing<IRelationship>(
      this.relationshipsSharedCollection,
      relationshipItem.relationship
    );
    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        relationshipItem.trackedEntityInstance
      );
    this.programInstancesSharedCollection = this.programInstanceService.addProgramInstanceToCollectionIfMissing<IProgramInstance>(
      this.programInstancesSharedCollection,
      relationshipItem.programInstance
    );
    this.programStageInstancesSharedCollection =
      this.programStageInstanceService.addProgramStageInstanceToCollectionIfMissing<IProgramStageInstance>(
        this.programStageInstancesSharedCollection,
        relationshipItem.programStageInstance
      );
  }

  protected loadRelationshipsOptions(): void {
    this.relationshipService
      .query()
      .pipe(map((res: HttpResponse<IRelationship[]>) => res.body ?? []))
      .pipe(
        map((relationships: IRelationship[]) =>
          this.relationshipService.addRelationshipToCollectionIfMissing<IRelationship>(relationships, this.relationshipItem?.relationship)
        )
      )
      .subscribe((relationships: IRelationship[]) => (this.relationshipsSharedCollection = relationships));

    this.trackedEntityInstanceService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityInstance[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityInstances: ITrackedEntityInstance[]) =>
          this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
            trackedEntityInstances,
            this.relationshipItem?.trackedEntityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );

    this.programInstanceService
      .query()
      .pipe(map((res: HttpResponse<IProgramInstance[]>) => res.body ?? []))
      .pipe(
        map((programInstances: IProgramInstance[]) =>
          this.programInstanceService.addProgramInstanceToCollectionIfMissing<IProgramInstance>(
            programInstances,
            this.relationshipItem?.programInstance
          )
        )
      )
      .subscribe((programInstances: IProgramInstance[]) => (this.programInstancesSharedCollection = programInstances));

    this.programStageInstanceService
      .query()
      .pipe(map((res: HttpResponse<IProgramStageInstance[]>) => res.body ?? []))
      .pipe(
        map((programStageInstances: IProgramStageInstance[]) =>
          this.programStageInstanceService.addProgramStageInstanceToCollectionIfMissing<IProgramStageInstance>(
            programStageInstances,
            this.relationshipItem?.programStageInstance
          )
        )
      )
      .subscribe((programStageInstances: IProgramStageInstance[]) => (this.programStageInstancesSharedCollection = programStageInstances));
  }
}
