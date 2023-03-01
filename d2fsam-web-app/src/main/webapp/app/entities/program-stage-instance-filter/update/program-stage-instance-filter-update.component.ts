import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramStageInstanceFilterFormService, ProgramStageInstanceFilterFormGroup } from './program-stage-instance-filter-form.service';
import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';
import { ProgramStageInstanceFilterService } from '../service/program-stage-instance-filter.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-program-stage-instance-filter-update',
  templateUrl: './program-stage-instance-filter-update.component.html',
})
export class ProgramStageInstanceFilterUpdateComponent implements OnInit {
  isSaving = false;
  programStageInstanceFilter: IProgramStageInstanceFilter | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: ProgramStageInstanceFilterFormGroup = this.programStageInstanceFilterFormService.createProgramStageInstanceFilterFormGroup();

  constructor(
    protected programStageInstanceFilterService: ProgramStageInstanceFilterService,
    protected programStageInstanceFilterFormService: ProgramStageInstanceFilterFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programStageInstanceFilter }) => {
      this.programStageInstanceFilter = programStageInstanceFilter;
      if (programStageInstanceFilter) {
        this.updateForm(programStageInstanceFilter);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programStageInstanceFilter = this.programStageInstanceFilterFormService.getProgramStageInstanceFilter(this.editForm);
    if (programStageInstanceFilter.id !== null) {
      this.subscribeToSaveResponse(this.programStageInstanceFilterService.update(programStageInstanceFilter));
    } else {
      this.subscribeToSaveResponse(this.programStageInstanceFilterService.create(programStageInstanceFilter));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramStageInstanceFilter>>): void {
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

  protected updateForm(programStageInstanceFilter: IProgramStageInstanceFilter): void {
    this.programStageInstanceFilter = programStageInstanceFilter;
    this.programStageInstanceFilterFormService.resetForm(this.editForm, programStageInstanceFilter);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      programStageInstanceFilter.createdBy,
      programStageInstanceFilter.updatedBy
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
            this.programStageInstanceFilter?.createdBy,
            this.programStageInstanceFilter?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
