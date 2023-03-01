import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import {
  TrackedEntityInstanceFilterFormService,
  TrackedEntityInstanceFilterFormGroup,
} from './tracked-entity-instance-filter-form.service';
import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import { TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { EventStatus } from 'app/entities/enumerations/event-status.model';

@Component({
  selector: 'app-tracked-entity-instance-filter-update',
  templateUrl: './tracked-entity-instance-filter-update.component.html',
})
export class TrackedEntityInstanceFilterUpdateComponent implements OnInit {
  isSaving = false;
  trackedEntityInstanceFilter: ITrackedEntityInstanceFilter | null = null;
  eventStatusValues = Object.keys(EventStatus);

  programsSharedCollection: IProgram[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: TrackedEntityInstanceFilterFormGroup = this.trackedEntityInstanceFilterFormService.createTrackedEntityInstanceFilterFormGroup();

  constructor(
    protected trackedEntityInstanceFilterService: TrackedEntityInstanceFilterService,
    protected trackedEntityInstanceFilterFormService: TrackedEntityInstanceFilterFormService,
    protected programService: ProgramService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackedEntityInstanceFilter }) => {
      this.trackedEntityInstanceFilter = trackedEntityInstanceFilter;
      if (trackedEntityInstanceFilter) {
        this.updateForm(trackedEntityInstanceFilter);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const trackedEntityInstanceFilter = this.trackedEntityInstanceFilterFormService.getTrackedEntityInstanceFilter(this.editForm);
    if (trackedEntityInstanceFilter.id !== null) {
      this.subscribeToSaveResponse(this.trackedEntityInstanceFilterService.update(trackedEntityInstanceFilter));
    } else {
      this.subscribeToSaveResponse(this.trackedEntityInstanceFilterService.create(trackedEntityInstanceFilter));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackedEntityInstanceFilter>>): void {
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

  protected updateForm(trackedEntityInstanceFilter: ITrackedEntityInstanceFilter): void {
    this.trackedEntityInstanceFilter = trackedEntityInstanceFilter;
    this.trackedEntityInstanceFilterFormService.resetForm(this.editForm, trackedEntityInstanceFilter);

    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      trackedEntityInstanceFilter.program
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      trackedEntityInstanceFilter.createdBy,
      trackedEntityInstanceFilter.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.trackedEntityInstanceFilter?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.trackedEntityInstanceFilter?.createdBy,
            this.trackedEntityInstanceFilter?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
