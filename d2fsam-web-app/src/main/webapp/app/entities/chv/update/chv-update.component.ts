import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ChvFormService, ChvFormGroup } from './chv-form.service';
import { IChv } from '../chv.model';
import { ChvService } from '../service/chv.service';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { UserDataService } from 'app/entities/user-data/service/user-data.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-chv-update',
  templateUrl: './chv-update.component.html',
})
export class ChvUpdateComponent implements OnInit {
  isSaving = false;
  chv: IChv | null = null;

  assignedTosCollection: IUserData[] = [];
  organisationUnitsSharedCollection: IOrganisationUnit[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ChvFormGroup = this.chvFormService.createChvFormGroup();

  constructor(
    protected chvService: ChvService,
    protected chvFormService: ChvFormService,
    protected userDataService: UserDataService,
    protected organisationUnitService: OrganisationUnitService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUserData = (o1: IUserData | null, o2: IUserData | null): boolean => this.userDataService.compareUserData(o1, o2);

  compareOrganisationUnit = (o1: IOrganisationUnit | null, o2: IOrganisationUnit | null): boolean =>
    this.organisationUnitService.compareOrganisationUnit(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ chv }) => {
      this.chv = chv;
      if (chv) {
        this.updateForm(chv);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const chv = this.chvFormService.getChv(this.editForm);
    if (chv.id !== null) {
      this.subscribeToSaveResponse(this.chvService.update(chv));
    } else {
      this.subscribeToSaveResponse(this.chvService.create(chv));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChv>>): void {
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

  protected updateForm(chv: IChv): void {
    this.chv = chv;
    this.chvFormService.resetForm(this.editForm, chv);

    this.assignedTosCollection = this.userDataService.addUserDataToCollectionIfMissing<IUserData>(
      this.assignedTosCollection,
      chv.assignedTo
    );
    this.organisationUnitsSharedCollection = this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
      this.organisationUnitsSharedCollection,
      chv.district,
      chv.homeSubvillage,
      chv.managingHf
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      chv.createdBy,
      chv.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userDataService
      .query({ filter: 'chv-is-null' })
      .pipe(map((res: HttpResponse<IUserData[]>) => res.body ?? []))
      .pipe(
        map((userData: IUserData[]) => this.userDataService.addUserDataToCollectionIfMissing<IUserData>(userData, this.chv?.assignedTo))
      )
      .subscribe((userData: IUserData[]) => (this.assignedTosCollection = userData));

    this.organisationUnitService
      .query()
      .pipe(map((res: HttpResponse<IOrganisationUnit[]>) => res.body ?? []))
      .pipe(
        map((organisationUnits: IOrganisationUnit[]) =>
          this.organisationUnitService.addOrganisationUnitToCollectionIfMissing<IOrganisationUnit>(
            organisationUnits,
            this.chv?.district,
            this.chv?.homeSubvillage,
            this.chv?.managingHf
          )
        )
      )
      .subscribe((organisationUnits: IOrganisationUnit[]) => (this.organisationUnitsSharedCollection = organisationUnits));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.chv?.createdBy, this.chv?.updatedBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
