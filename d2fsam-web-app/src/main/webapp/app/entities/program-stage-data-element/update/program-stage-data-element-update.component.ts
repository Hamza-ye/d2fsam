import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramStageDataElementFormService, ProgramStageDataElementFormGroup } from './program-stage-data-element-form.service';
import { IProgramStageDataElement } from '../program-stage-data-element.model';
import { ProgramStageDataElementService } from '../service/program-stage-data-element.service';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { ProgramStageService } from 'app/entities/program-stage/service/program-stage.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-program-stage-data-element-update',
  templateUrl: './program-stage-data-element-update.component.html',
})
export class ProgramStageDataElementUpdateComponent implements OnInit {
  isSaving = false;
  programStageDataElement: IProgramStageDataElement | null = null;

  programStagesSharedCollection: IProgramStage[] = [];
  dataElementsSharedCollection: IDataElement[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ProgramStageDataElementFormGroup = this.programStageDataElementFormService.createProgramStageDataElementFormGroup();

  constructor(
    protected programStageDataElementService: ProgramStageDataElementService,
    protected programStageDataElementFormService: ProgramStageDataElementFormService,
    protected programStageService: ProgramStageService,
    protected dataElementService: DataElementService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgramStage = (o1: IProgramStage | null, o2: IProgramStage | null): boolean =>
    this.programStageService.compareProgramStage(o1, o2);

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStageDataElement }) => {
      this.programStageDataElement = programStageDataElement;
      if (programStageDataElement) {
        this.updateForm(programStageDataElement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programStageDataElement = this.programStageDataElementFormService.getProgramStageDataElement(this.editForm);
    if (programStageDataElement.id !== null) {
      this.subscribeToSaveResponse(this.programStageDataElementService.update(programStageDataElement));
    } else {
      this.subscribeToSaveResponse(this.programStageDataElementService.create(programStageDataElement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramStageDataElement>>): void {
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

  protected updateForm(programStageDataElement: IProgramStageDataElement): void {
    this.programStageDataElement = programStageDataElement;
    this.programStageDataElementFormService.resetForm(this.editForm, programStageDataElement);

    this.programStagesSharedCollection = this.programStageService.addProgramStageToCollectionIfMissing<IProgramStage>(
      this.programStagesSharedCollection,
      programStageDataElement.programStage
    );
    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      programStageDataElement.dataElement
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      programStageDataElement.createdBy,
      programStageDataElement.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.programStageService
      .query()
      .pipe(map((res: HttpResponse<IProgramStage[]>) => res.body ?? []))
      .pipe(
        map((programStages: IProgramStage[]) =>
          this.programStageService.addProgramStageToCollectionIfMissing<IProgramStage>(
            programStages,
            this.programStageDataElement?.programStage
          )
        )
      )
      .subscribe((programStages: IProgramStage[]) => (this.programStagesSharedCollection = programStages));

    this.dataElementService
      .query()
      .pipe(map((res: HttpResponse<IDataElement[]>) => res.body ?? []))
      .pipe(
        map((dataElements: IDataElement[]) =>
          this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(dataElements, this.programStageDataElement?.dataElement)
        )
      )
      .subscribe((dataElements: IDataElement[]) => (this.dataElementsSharedCollection = dataElements));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.programStageDataElement?.createdBy,
            this.programStageDataElement?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
