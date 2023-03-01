import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramInstanceFormService, ProgramInstanceFormGroup } from './program-instance-form.service';
import { IProgramInstance } from '../program-instance.model';
import { ProgramInstanceService } from '../service/program-instance.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IComment } from 'app/entities/comment/comment.model';
import { CommentService } from 'app/entities/comment/service/comment.service';
import { PeriodLabel } from 'app/entities/enumerations/period-label.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

@Component({
  selector: 'app-program-instance-update',
  templateUrl: './program-instance-update.component.html',
})
export class ProgramInstanceUpdateComponent implements OnInit {
  isSaving = false;
  programInstance: IProgramInstance | null = null;
  periodLabelValues = Object.keys(PeriodLabel);
  eventStatusValues = Object.keys(EventStatus);

  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];
  programsSharedCollection: IProgram[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  activitiesSharedCollection: IActivity[] = [];
  usersSharedCollection: IUser[] = [];
  commentsSharedCollection: IComment[] = [];

  editForm: ProgramInstanceFormGroup = this.programInstanceFormService.createProgramInstanceFormGroup();

  constructor(
    protected programInstanceService: ProgramInstanceService,
    protected programInstanceFormService: ProgramInstanceFormService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected programService: ProgramService,
    protected organisationUnitService: OrganisationUnitService,
    protected activityService: ActivityService,
    protected userService: UserService,
    protected commentService: CommentService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareComment = (o1: IComment | null, o2: IComment | null): boolean => this.commentService.compareComment(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programInstance }) => {
      this.programInstance = programInstance;
      if (programInstance) {
        this.updateForm(programInstance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programInstance = this.programInstanceFormService.getProgramInstance(this.editForm);
    if (programInstance.id !== null) {
      this.subscribeToSaveResponse(this.programInstanceService.update(programInstance));
    } else {
      this.subscribeToSaveResponse(this.programInstanceService.create(programInstance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramInstance>>): void {
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

  protected updateForm(programInstance: IProgramInstance): void {
    this.programInstance = programInstance;
    this.programInstanceFormService.resetForm(this.editForm, programInstance);

    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        programInstance.entityInstance
      );
    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      programInstance.program
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      programInstance.organisationUnit
    );
    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      programInstance.activity
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      programInstance.createdBy,
      programInstance.updatedBy,
      programInstance.approvedBy
    );
    this.commentsSharedCollection = this.commentService.addCommentToCollectionIfMissing<IComment>(
      this.commentsSharedCollection,
      ...(programInstance.comments ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trackedEntityInstanceService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityInstance[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityInstances: ITrackedEntityInstance[]) =>
          this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
            trackedEntityInstances,
            this.programInstance?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );

    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.programInstance?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.programInstance?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) =>
          this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, this.programInstance?.activity)
        )
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.programInstance?.createdBy,
            this.programInstance?.updatedBy,
            this.programInstance?.approvedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.commentService
      .query()
      .pipe(map((res: HttpResponse<IComment[]>) => res.body ?? []))
      .pipe(
        map((comments: IComment[]) =>
          this.commentService.addCommentToCollectionIfMissing<IComment>(comments, ...(this.programInstance?.comments ?? []))
        )
      )
      .subscribe((comments: IComment[]) => (this.commentsSharedCollection = comments));
  }
}
