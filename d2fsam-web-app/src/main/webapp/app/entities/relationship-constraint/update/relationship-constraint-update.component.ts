import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RelationshipConstraintFormService, RelationshipConstraintFormGroup } from './relationship-constraint-form.service';
import { IRelationshipConstraint } from '../relationship-constraint.model';
import { RelationshipConstraintService } from '../service/relationship-constraint.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { ProgramStageService } from 'app/entities/program-stage/service/program-stage.service';
import { RelationshipEntity } from 'app/entities/enumerations/relationship-entity.model';

@Component({
  selector: 'app-relationship-constraint-update',
  templateUrl: './relationship-constraint-update.component.html',
})
export class RelationshipConstraintUpdateComponent implements OnInit {
  isSaving = false;
  relationshipConstraint: IRelationshipConstraint | null = null;
  relationshipEntityValues = Object.keys(RelationshipEntity);

  trackedEntityTypesSharedCollection: ITrackedEntityType[] = [];
  programsSharedCollection: IProgram[] = [];
  programStagesSharedCollection: IProgramStage[] = [];

  editForm: RelationshipConstraintFormGroup = this.relationshipConstraintFormService.createRelationshipConstraintFormGroup();

  constructor(
    protected relationshipConstraintService: RelationshipConstraintService,
    protected relationshipConstraintFormService: RelationshipConstraintFormService,
    protected trackedEntityTypeService: TrackedEntityTypeService,
    protected programService: ProgramService,
    protected programStageService: ProgramStageService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTrackedEntityType = (o1: ITrackedEntityType | null, o2: ITrackedEntityType | null): boolean =>
    this.trackedEntityTypeService.compareTrackedEntityType(o1, o2);

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareProgramStage = (o1: IProgramStage | null, o2: IProgramStage | null): boolean =>
    this.programStageService.compareProgramStage(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationshipConstraint }) => {
      this.relationshipConstraint = relationshipConstraint;
      if (relationshipConstraint) {
        this.updateForm(relationshipConstraint);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relationshipConstraint = this.relationshipConstraintFormService.getRelationshipConstraint(this.editForm);
    if (relationshipConstraint.id !== null) {
      this.subscribeToSaveResponse(this.relationshipConstraintService.update(relationshipConstraint));
    } else {
      this.subscribeToSaveResponse(this.relationshipConstraintService.create(relationshipConstraint));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelationshipConstraint>>): void {
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

  protected updateForm(relationshipConstraint: IRelationshipConstraint): void {
    this.relationshipConstraint = relationshipConstraint;
    this.relationshipConstraintFormService.resetForm(this.editForm, relationshipConstraint);

    this.trackedEntityTypesSharedCollection = this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
      this.trackedEntityTypesSharedCollection,
      relationshipConstraint.trackedEntityType
    );
    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      relationshipConstraint.program
    );
    this.programStagesSharedCollection = this.programStageService.addProgramStageToCollectionIfMissing<IProgramStage>(
      this.programStagesSharedCollection,
      relationshipConstraint.programStage
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trackedEntityTypeService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityType[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityTypes: ITrackedEntityType[]) =>
          this.trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing<ITrackedEntityType>(
            trackedEntityTypes,
            this.relationshipConstraint?.trackedEntityType
          )
        )
      )
      .subscribe((trackedEntityTypes: ITrackedEntityType[]) => (this.trackedEntityTypesSharedCollection = trackedEntityTypes));

    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.relationshipConstraint?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.programStageService
      .query()
      .pipe(map((res: HttpResponse<IProgramStage[]>) => res.body ?? []))
      .pipe(
        map((programStages: IProgramStage[]) =>
          this.programStageService.addProgramStageToCollectionIfMissing<IProgramStage>(
            programStages,
            this.relationshipConstraint?.programStage
          )
        )
      )
      .subscribe((programStages: IProgramStage[]) => (this.programStagesSharedCollection = programStages));
  }
}
