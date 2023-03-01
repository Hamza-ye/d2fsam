import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DemographicDataSourceFormService, DemographicDataSourceFormGroup } from './demographic-data-source-form.service';
import { IDemographicDataSource } from '../demographic-data-source.model';
import { DemographicDataSourceService } from '../service/demographic-data-source.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-demographic-data-source-update',
  templateUrl: './demographic-data-source-update.component.html',
})
export class DemographicDataSourceUpdateComponent implements OnInit {
  isSaving = false;
  demographicDataSource: IDemographicDataSource | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: DemographicDataSourceFormGroup = this.demographicDataSourceFormService.createDemographicDataSourceFormGroup();

  constructor(
    protected demographicDataSourceService: DemographicDataSourceService,
    protected demographicDataSourceFormService: DemographicDataSourceFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demographicDataSource }) => {
      this.demographicDataSource = demographicDataSource;
      if (demographicDataSource) {
        this.updateForm(demographicDataSource);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const demographicDataSource = this.demographicDataSourceFormService.getDemographicDataSource(this.editForm);
    if (demographicDataSource.id !== null) {
      this.subscribeToSaveResponse(this.demographicDataSourceService.update(demographicDataSource));
    } else {
      this.subscribeToSaveResponse(this.demographicDataSourceService.create(demographicDataSource));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDemographicDataSource>>): void {
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

  protected updateForm(demographicDataSource: IDemographicDataSource): void {
    this.demographicDataSource = demographicDataSource;
    this.demographicDataSourceFormService.resetForm(this.editForm, demographicDataSource);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      demographicDataSource.createdBy,
      demographicDataSource.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.demographicDataSource?.createdBy,
            this.demographicDataSource?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
