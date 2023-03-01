import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramStageFormService, ProgramStageFormGroup } from './program-stage-form.service';
import { IProgramStage } from '../program-stage.model';
import { ProgramStageService } from '../service/program-stage.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ValidationStrategy } from 'app/entities/enumerations/validation-strategy.model';
import { FeatureType } from 'app/entities/enumerations/feature-type.model';

@Component({
  selector: 'app-program-stage-update',
  templateUrl: './program-stage-update.component.html',
})
export class ProgramStageUpdateComponent implements OnInit {
  isSaving = false;
  programStage: IProgramStage | null = null;
  validationStrategyValues = Object.keys(ValidationStrategy);
  featureTypeValues = Object.keys(FeatureType);

  periodsSharedCollection: IPeriod[] = [];
  programsSharedCollection: IProgram[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ProgramStageFormGroup = this.programStageFormService.createProgramStageFormGroup();

  constructor(
    protected programStageService: ProgramStageService,
    protected programStageFormService: ProgramStageFormService,
    protected periodService: PeriodService,
    protected programService: ProgramService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStage }) => {
      this.programStage = programStage;
      if (programStage) {
        this.updateForm(programStage);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programStage = this.programStageFormService.getProgramStage(this.editForm);
    if (programStage.id !== null) {
      this.subscribeToSaveResponse(this.programStageService.update(programStage));
    } else {
      this.subscribeToSaveResponse(this.programStageService.create(programStage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramStage>>): void {
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

  protected updateForm(programStage: IProgramStage): void {
    this.programStage = programStage;
    this.programStageFormService.resetForm(this.editForm, programStage);

    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      programStage.periodType
    );
    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      programStage.program
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      programStage.createdBy,
      programStage.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.programStage?.periodType)))
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));

    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) => this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.programStage?.program))
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.programStage?.createdBy, this.programStage?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
