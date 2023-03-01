import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TeamGroupFormService, TeamGroupFormGroup } from './team-group-form.service';
import { ITeamGroup } from '../team-group.model';
import { TeamGroupService } from '../service/team-group.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';

@Component({
  selector: 'app-team-group-update',
  templateUrl: './team-group-update.component.html',
})
export class TeamGroupUpdateComponent implements OnInit {
  isSaving = false;
  teamGroup: ITeamGroup | null = null;

  teamsSharedCollection: ITeam[] = [];

  editForm: TeamGroupFormGroup = this.teamGroupFormService.createTeamGroupFormGroup();

  constructor(
    protected teamGroupService: TeamGroupService,
    protected teamGroupFormService: TeamGroupFormService,
    protected teamService: TeamService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTeam = (o1: ITeam | null, o2: ITeam | null): boolean => this.teamService.compareTeam(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamGroup }) => {
      this.teamGroup = teamGroup;
      if (teamGroup) {
        this.updateForm(teamGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamGroup = this.teamGroupFormService.getTeamGroup(this.editForm);
    if (teamGroup.id !== null) {
      this.subscribeToSaveResponse(this.teamGroupService.update(teamGroup));
    } else {
      this.subscribeToSaveResponse(this.teamGroupService.create(teamGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamGroup>>): void {
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

  protected updateForm(teamGroup: ITeamGroup): void {
    this.teamGroup = teamGroup;
    this.teamGroupFormService.resetForm(this.editForm, teamGroup);

    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing<ITeam>(
      this.teamsSharedCollection,
      ...(teamGroup.members ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing<ITeam>(teams, ...(this.teamGroup?.members ?? []))))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));
  }
}
