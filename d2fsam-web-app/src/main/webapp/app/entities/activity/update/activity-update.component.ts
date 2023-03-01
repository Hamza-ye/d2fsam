import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ActivityFormService, ActivityFormGroup } from './activity-form.service';
import { IActivity } from '../activity.model';
import { ActivityService } from '../service/activity.service';
import { IProject } from 'app/entities/project/project.model';
import { ProjectService } from 'app/entities/project/service/project.service';
import { IDemographicData } from 'app/entities/demographic-data/demographic-data.model';
import { DemographicDataService } from 'app/entities/demographic-data/service/demographic-data.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

@Component({
  selector: 'app-activity-update',
  templateUrl: './activity-update.component.html',
})
export class ActivityUpdateComponent implements OnInit {
  isSaving = false;
  activity: IActivity | null = null;

  projectsSharedCollection: IProject[] = [];
  activitiesSharedCollection: IActivity[] = [];
  demographicDataSharedCollection: IDemographicData[] = [];
  usersSharedCollection: IUser[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];

  editForm: ActivityFormGroup = this.activityFormService.createActivityFormGroup();

  constructor(
    protected activityService: ActivityService,
    protected activityFormService: ActivityFormService,
    protected projectService: ProjectService,
    protected demographicDataService: DemographicDataService,
    protected userService: UserService,
    protected organisationUnitService: OrganisationUnitService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProject = (o1: IProject | null, o2: IProject | null): boolean => this.projectService.compareProject(o1, o2);

  compareActivity = (o1: IActivity | null, o2: IActivity | null): boolean => this.activityService.compareActivity(o1, o2);

  compareDemographicData = (o1: IDemographicData | null, o2: IDemographicData | null): boolean =>
    this.demographicDataService.compareDemographicData(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activity }) => {
      this.activity = activity;
      if (activity) {
        this.updateForm(activity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const activity = this.activityFormService.getActivity(this.editForm);
    if (activity.id !== null) {
      this.subscribeToSaveResponse(this.activityService.update(activity));
    } else {
      this.subscribeToSaveResponse(this.activityService.create(activity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>): void {
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

  protected updateForm(activity: IActivity): void {
    this.activity = activity;
    this.activityFormService.resetForm(this.editForm, activity);

    this.projectsSharedCollection = this.projectService.addProjectToCollectionIfMissing<IProject>(
      this.projectsSharedCollection,
      activity.project
    );
    this.activitiesSharedCollection = this.activityService.addActivityToCollectionIfMissing<IActivity>(
      this.activitiesSharedCollection,
      activity.activityUsedAsTarget
    );
    this.demographicDataSharedCollection = this.demographicDataService.addDemographicDataToCollectionIfMissing<IDemographicData>(
      this.demographicDataSharedCollection,
      activity.demographicData
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      activity.createdBy,
      activity.updatedBy
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      ...(activity.targetedOrganisationUnits ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.projectService
      .query()
      .pipe(map((res: HttpResponse<IProject[]>) => res.body ?? []))
      .pipe(map((projects: IProject[]) => this.projectService.addProjectToCollectionIfMissing<IProject>(projects, this.activity?.project)))
      .subscribe((projects: IProject[]) => (this.projectsSharedCollection = projects));

    this.activityService
      .query()
      .pipe(map((res: HttpResponse<IActivity[]>) => res.body ?? []))
      .pipe(
        map((activities: IActivity[]) =>
          this.activityService.addActivityToCollectionIfMissing<IActivity>(activities, this.activity?.activityUsedAsTarget)
        )
      )
      .subscribe((activities: IActivity[]) => (this.activitiesSharedCollection = activities));

    this.demographicDataService
      .query()
      .pipe(map((res: HttpResponse<IDemographicData[]>) => res.body ?? []))
      .pipe(
        map((demographicData: IDemographicData[]) =>
          this.demographicDataService.addDemographicDataToCollectionIfMissing<IDemographicData>(
            demographicData,
            this.activity?.demographicData
          )
        )
      )
      .subscribe((demographicData: IDemographicData[]) => (this.demographicDataSharedCollection = demographicData));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.activity?.createdBy, this.activity?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            ...(this.activity?.targetedOrganisationUnits ?? [])
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));
  }
}
