import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MalariaCaseFormService, MalariaCaseFormGroup } from './malaria-case-form.service';
import { IMalariaCase } from '../malaria-case.model';
import { MalariaCaseService } from '../service/malaria-case.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { Gender } from 'app/entities/enumerations/gender.model';
import { MalariaTestResult } from 'app/entities/enumerations/malaria-test-result.model';
import { Severity } from 'app/entities/enumerations/severity.model';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

@Component({
  selector: 'app-malaria-case-update',
  templateUrl: './malaria-case-update.component.html',
})
export class MalariaCaseUpdateComponent implements OnInit {
  isSaving = false;
  malariaCase: IMalariaCase | null = null;
  genderValues = Object.keys(Gender);
  malariaTestResultValues = Object.keys(MalariaTestResult);
  severityValues = Object.keys(Severity);
  eventStatusValues = Object.keys(EventStatus);

  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: MalariaCaseFormGroup = this.malariaCaseFormService.createMalariaCaseFormGroup();

  constructor(
    protected malariaCaseService: MalariaCaseService,
    protected malariaCaseFormService: MalariaCaseFormService,
    protected organisationUnitService: OrganisationUnitService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ malariaCase }) => {
      this.malariaCase = malariaCase;
      if (malariaCase) {
        this.updateForm(malariaCase);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const malariaCase = this.malariaCaseFormService.getMalariaCase(this.editForm);
    if (malariaCase.id !== null) {
      this.subscribeToSaveResponse(this.malariaCaseService.update(malariaCase));
    } else {
      this.subscribeToSaveResponse(this.malariaCaseService.create(malariaCase));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMalariaCase>>): void {
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

  protected updateForm(malariaCase: IMalariaCase): void {
    this.malariaCase = malariaCase;
    this.malariaCaseFormService.resetForm(this.editForm, malariaCase);

    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      malariaCase.subVillage
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      malariaCase.createdBy,
      malariaCase.updatedBy
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
            this.malariaCase?.subVillage
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.malariaCase?.createdBy, this.malariaCase?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
