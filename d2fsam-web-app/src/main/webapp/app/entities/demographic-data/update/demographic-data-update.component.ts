import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DemographicDataFormService, DemographicDataFormGroup } from './demographic-data-form.service';
import { IDemographicData } from '../demographic-data.model';
import { DemographicDataService } from '../service/demographic-data.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IDemographicDataSource } from 'app/entities/demographic-data-source/demographic-data-source.model';
import { DemographicDataSourceService } from 'app/entities/demographic-data-source/service/demographic-data-source.service';
import { DemographicDataLevel } from 'app/entities/enumerations/demographic-data-level.model';

@Component({
  selector: 'app-demographic-data-update',
  templateUrl: './demographic-data-update.component.html',
})
export class DemographicDataUpdateComponent implements OnInit {
  isSaving = false;
  demographicData: IDemographicData | null = null;
  demographicDataLevelValues = Object.keys(DemographicDataLevel);

  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  usersSharedCollection: IUser[] = [];
  demographicDataSourcesSharedCollection: IDemographicDataSource[] = [];

  editForm: DemographicDataFormGroup = this.demographicDataFormService.createDemographicDataFormGroup();

  constructor(
    protected demographicDataService: DemographicDataService,
    protected demographicDataFormService: DemographicDataFormService,
    protected organisationUnitService: OrganisationUnitService,
    protected userService: UserService,
    protected demographicDataSourceService: DemographicDataSourceService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareDemographicDataSource = (o1: IDemographicDataSource | null, o2: IDemographicDataSource | null): boolean =>
    this.demographicDataSourceService.compareDemographicDataSource(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demographicData }) => {
      this.demographicData = demographicData;
      if (demographicData) {
        this.updateForm(demographicData);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const demographicData = this.demographicDataFormService.getDemographicData(this.editForm);
    if (demographicData.id !== null) {
      this.subscribeToSaveResponse(this.demographicDataService.update(demographicData));
    } else {
      this.subscribeToSaveResponse(this.demographicDataService.create(demographicData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDemographicData>>): void {
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

  protected updateForm(demographicData: IDemographicData): void {
    this.demographicData = demographicData;
    this.demographicDataFormService.resetForm(this.editForm, demographicData);

    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      demographicData.organisationUnit
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      demographicData.createdBy,
      demographicData.updatedBy
    );
    this.demographicDataSourcesSharedCollection =
      this.demographicDataSourceService.addDemographicDataSourceToCollectionIfMissing<IDemographicDataSource>(
        this.demographicDataSourcesSharedCollection,
        demographicData.source
      );
  }

  protected loadRelationshipsOptions(): void {
    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.demographicData?.organisationUnit
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.demographicData?.createdBy, this.demographicData?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.demographicDataSourceService
      .query()
      .pipe(map((res: HttpResponse<IDemographicDataSource[]>) => res.body ?? []))
      .pipe(
        map((demographicDataSources: IDemographicDataSource[]) =>
          this.demographicDataSourceService.addDemographicDataSourceToCollectionIfMissing<IDemographicDataSource>(
            demographicDataSources,
            this.demographicData?.source
          )
        )
      )
      .subscribe(
        (demographicDataSources: IDemographicDataSource[]) => (this.demographicDataSourcesSharedCollection = demographicDataSources)
      );
  }
}
