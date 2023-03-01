import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramStageInstanceFormService, ProgramStageInstanceFormGroup } from './program-stage-instance-form.service';
import { IProgramStageInstance } from '../program-stage-instance.model';
import { ProgramStageInstanceService } from '../service/program-stage-instance.service';
import { IProgramInstance } from 'app/entities/program-instance/program-instance.model';
import { ProgramInstanceService } from 'app/entities/program-instance/service/program-instance.service';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { ProgramStageService } from 'app/entities/program-stage/service/program-stage.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IComment } from 'app/entities/comment/comment.model';
import { CommentService } from 'app/entities/comment/service/comment.service';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

@Component({
  selector: 'app-program-stage-instance-update',
  templateUrl: './program-stage-instance-update.component.html',
})
export class ProgramStageInstanceUpdateComponent implements OnInit {
  isSaving = false;
  programStageInstance: IProgramStageInstance | null = null;
  eventStatusValues = Object.keys(EventStatus);

  programInstancesSharedCollection: IProgramInstance[] = [];
  programStagesSharedCollection: IProgramStage[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  usersSharedCollection: IUser[] = [];
  periodsSharedCollection: IPeriod[] = [];
  commentsSharedCollection: IComment[] = [];

  editForm: ProgramStageInstanceFormGroup = this.programStageInstanceFormService.createProgramStageInstanceFormGroup();

  constructor(
    protected programStageInstanceService: ProgramStageInstanceService,
    protected programStageInstanceFormService: ProgramStageInstanceFormService,
    protected programInstanceService: ProgramInstanceService,
    protected programStageService: ProgramStageService,
    protected organisationUnitService: OrganisationUnitService,
    protected userService: UserService,
    protected periodService: PeriodService,
    protected commentService: CommentService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgramInstance = (o1: IProgramInstance | null, o2: IProgramInstance | null): boolean =>
    this.programInstanceService.compareProgramInstance(o1, o2);

  compareProgramStage = (o1: IProgramStage | null, o2: IProgramStage | null): boolean =>
    this.programStageService.compareProgramStage(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  compareComment = (o1: IComment | null, o2: IComment | null): boolean => this.commentService.compareComment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStageInstance }) => {
      this.programStageInstance = programStageInstance;
      if (programStageInstance) {
        this.updateForm(programStageInstance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programStageInstance = this.programStageInstanceFormService.getProgramStageInstance(this.editForm);
    if (programStageInstance.id !== null) {
      this.subscribeToSaveResponse(this.programStageInstanceService.update(programStageInstance));
    } else {
      this.subscribeToSaveResponse(this.programStageInstanceService.create(programStageInstance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramStageInstance>>): void {
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

  protected updateForm(programStageInstance: IProgramStageInstance): void {
    this.programStageInstance = programStageInstance;
    this.programStageInstanceFormService.resetForm(this.editForm, programStageInstance);

    this.programInstancesSharedCollection = this.programInstanceService.addProgramInstanceToCollectionIfMissing<IProgramInstance>(
      this.programInstancesSharedCollection,
      programStageInstance.programInstance
    );
    this.programStagesSharedCollection = this.programStageService.addProgramStageToCollectionIfMissing<IProgramStage>(
      this.programStagesSharedCollection,
      programStageInstance.programStage
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      programStageInstance.organisationUnit
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      programStageInstance.assignedUser,
      programStageInstance.createdBy,
      programStageInstance.updatedBy,
      programStageInstance.approvedBy
    );
    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      programStageInstance.period
    );
    this.commentsSharedCollection = this.commentService.addCommentToCollectionIfMissing<IComment>(
      this.commentsSharedCollection,
      ...(programStageInstance.comments ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.programInstanceService
      .query()
      .pipe(map((res: HttpResponse<IProgramInstance[]>) => res.body ?? []))
      .pipe(
        map((programInstances: IProgramInstance[]) =>
          this.programInstanceService.addProgramInstanceToCollectionIfMissing<IProgramInstance>(
            programInstances,
            this.programStageInstance?.programInstance
          )
        )
      )
      .subscribe((programInstances: IProgramInstance[]) => (this.programInstancesSharedCollection = programInstances));

    this.programStageService
      .query()
      .pipe(map((res: HttpResponse<IProgramStage[]>) => res.body ?? []))
      .pipe(
        map((programStages: IProgramStage[]) =>
          this.programStageService.addProgramStageToCollectionIfMissing<IProgramStage>(
            programStages,
            this.programStageInstance?.programStage
          )
        )
      )
      .subscribe((programStages: IProgramStage[]) => (this.programStagesSharedCollection = programStages));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.programStageInstance?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.programStageInstance?.assignedUser,
            this.programStageInstance?.createdBy,
            this.programStageInstance?.updatedBy,
            this.programStageInstance?.approvedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(
        map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.programStageInstance?.period))
      )
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));

    this.commentService
      .query()
      .pipe(map((res: HttpResponse<IComment[]>) => res.body ?? []))
      .pipe(
        map((comments: IComment[]) =>
          this.commentService.addCommentToCollectionIfMissing<IComment>(comments, ...(this.programStageInstance?.comments ?? []))
        )
      )
      .subscribe((comments: IComment[]) => (this.commentsSharedCollection = comments));
  }
}
