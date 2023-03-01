import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TeamFormService, TeamFormGroup } from './team-form.service';
import { ITeam } from '../team.model';
import { TeamService } from '../service/team.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { UserDataService } from 'app/entities/user-data/service/user-data.service';
import { TeamType } from 'app/entities/enumerations/team-type.model';

@Component({
  selector: 'app-team-update',
  templateUrl: './team-update.component.html',
})
export class TeamUpdateComponent implements OnInit {
  isSaving = false;
  team: ITeam | null = null;
  teamTypeValues = Object.keys(TeamType);

  activitiesSharedCollection: IActivity[] = [];
  usersSharedCollection: IUser[] = [];
  userDataSharedCollection: IUserData[] = [];
  teamsSharedCollection: ITeam[] = [];

  editForm: TeamFormGroup = this.teamFormService.createTeamFormGroup();

  constructor(
    protected teamService: TeamService,
    protected teamFormService: TeamFormService,
    protected activityService: ActivityService,
    protected userService: UserService,
    protected userDataService: UserDataService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareUserData = (o1: IUserData | null, o2: IUserData | null): boolean => this.userDataService.compareUserData(o1, o2);

  compareTeam = (o1: ITeam | null, o2: ITeam | null): boolean => this.teamService.compareTeam(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ team }) => {
      this.team = team;
      if (team) {
        this.updateForm(team);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const team = this.teamFormService.getTeam(this.editForm);
    if (team.id !== null) {
      this.subscribeToSaveResponse(this.teamService.update(team));
    } else {
      this.subscribeToSaveResponse(this.teamService.create(team));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeam>>): void {
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

  protected updateForm(team: ITeam): void {
    this.team = team;
    this.teamFormService.resetForm(this.editForm, team);

    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      team.activity
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      team.createdBy,
      team.updatedBy
    );
    this.userDataSharedCollection = this.userDataService.addUserDataToCollectionIfMissing<IUserData>(
      this.userDataSharedCollection,
      ...(team.members ?? [])
    );
    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing<ITeam>(
      this.teamsSharedCollection,
      ...(team.managedTeams ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) => this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, this.team?.activity))
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.team?.createdBy, this.team?.updatedBy))
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.userDataService
      .query()
      .pipe(map((res: HttpResponse<IUserData[]>) => res.body ?? []))
      .pipe(
        map((userData: IUserData[]) =>
          this.userDataService.addUserDataToCollectionIfMissing<IUserData>(userData, ...(this.team?.members ?? []))
        )
      )
      .subscribe((userData: IUserData[]) => (this.userDataSharedCollection = userData));

    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing<ITeam>(teams, ...(this.team?.managedTeams ?? []))))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));
  }
}
