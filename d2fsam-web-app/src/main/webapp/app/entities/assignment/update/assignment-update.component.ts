import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AssignmentFormService, AssignmentFormGroup } from './assignment-form.service';
import { IAssignment } from '../assignment.model';
import { AssignmentService } from '../service/assignment.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { IPeriod } from 'app/entities/period/period.model';
import { PeriodService } from 'app/entities/period/service/period.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { PeriodLabel } from 'app/entities/enumerations/period-label.model';
import { TargetSource } from 'app/entities/enumerations/target-source.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

@Component({
  selector: 'app-assignment-update',
  templateUrl: './assignment-update.component.html',
})
export class AssignmentUpdateComponent implements OnInit {
  isSaving = false;
  assignment: IAssignment | null = null;
  periodLabelValues = Object.keys(PeriodLabel);
  targetSourceValues = Object.keys(TargetSource);
  eventStatusValues = Object.keys(EventStatus);

  activitiesSharedCollection: IActivity[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  teamsSharedCollection: ITeam[] = [];
  periodsSharedCollection: IPeriod[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: AssignmentFormGroup = this.assignmentFormService.createAssignmentFormGroup();

  constructor(
    protected assignmentService: AssignmentService,
    protected assignmentFormService: AssignmentFormService,
    protected activityService: ActivityService,
    protected organisationUnitService: OrganisationUnitService,
    protected teamService: TeamService,
    protected periodService: PeriodService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareTeam = (o1: ITeam | null, o2: ITeam | null): boolean => this.teamService.compareTeam(o1, o2);

  comparePeriod = (o1: IPeriod | null, o2: IPeriod | null): boolean => this.periodService.comparePeriod(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ assignment }) => {
      this.assignment = assignment;
      if (assignment) {
        this.updateForm(assignment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const assignment = this.assignmentFormService.getAssignment(this.editForm);
    if (assignment.id !== null) {
      this.subscribeToSaveResponse(this.assignmentService.update(assignment));
    } else {
      this.subscribeToSaveResponse(this.assignmentService.create(assignment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAssignment>>): void {
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

  protected updateForm(assignment: IAssignment): void {
    this.assignment = assignment;
    this.assignmentFormService.resetForm(this.editForm, assignment);

    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      assignment.activity
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      assignment.organisationUnit
    );
    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing<ITeam>(this.teamsSharedCollection, assignment.assignedTeam);
    this.periodsSharedCollection = this.periodService.addPeriodToCollectionIfMissing<IPeriod>(
      this.periodsSharedCollection,
      assignment.period
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      assignment.createdBy,
      assignment.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) =>
          this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, this.assignment?.activity)
        )
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.assignment?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing<ITeam>(teams, this.assignment?.assignedTeam)))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));

    this.periodService
      .query()
      .pipe(map((res: HttpResponse<IPeriod[]>) => res.body ?? []))
      .pipe(map((periods: IPeriod[]) => this.periodService.addPeriodToCollectionIfMissing<IPeriod>(periods, this.assignment?.period)))
      .subscribe((periods: IPeriod[]) => (this.periodsSharedCollection = periods));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.assignment?.createdBy, this.assignment?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
