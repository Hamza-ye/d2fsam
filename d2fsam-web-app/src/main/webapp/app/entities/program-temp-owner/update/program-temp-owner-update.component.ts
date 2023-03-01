import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProgramTempOwnerFormService, ProgramTempOwnerFormGroup } from './program-temp-owner-form.service';
import { IProgramTempOwner } from '../program-temp-owner.model';
import { ProgramTempOwnerService } from '../service/program-temp-owner.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-program-temp-owner-update',
  templateUrl: './program-temp-owner-update.component.html',
})
export class ProgramTempOwnerUpdateComponent implements OnInit {
  isSaving = false;
  programTempOwner: IProgramTempOwner | null = null;

  programsSharedCollection: IProgram[] = [];
  trackedEntityInstancesSharedCollection: ITrackedEntityInstance[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ProgramTempOwnerFormGroup = this.programTempOwnerFormService.createProgramTempOwnerFormGroup();

  constructor(
    protected programTempOwnerService: ProgramTempOwnerService,
    protected programTempOwnerFormService: ProgramTempOwnerFormService,
    protected programService: ProgramService,
    protected trackedEntityInstanceService: TrackedEntityInstanceService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareTrackedEntityInstance = (o1: ITrackedEntityInstance | null, o2: ITrackedEntityInstance | null): boolean =>
    this.trackedEntityInstanceService.compareTrackedEntityInstance(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programTempOwner }) => {
      this.programTempOwner = programTempOwner;
      if (programTempOwner) {
        this.updateForm(programTempOwner);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programTempOwner = this.programTempOwnerFormService.getProgramTempOwner(this.editForm);
    if (programTempOwner.id !== null) {
      this.subscribeToSaveResponse(this.programTempOwnerService.update(programTempOwner));
    } else {
      this.subscribeToSaveResponse(this.programTempOwnerService.create(programTempOwner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramTempOwner>>): void {
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

  protected updateForm(programTempOwner: IProgramTempOwner): void {
    this.programTempOwner = programTempOwner;
    this.programTempOwnerFormService.resetForm(this.editForm, programTempOwner);

    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      programTempOwner.program
    );
    this.trackedEntityInstancesSharedCollection =
      this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
        this.trackedEntityInstancesSharedCollection,
        programTempOwner.entityInstance
      );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, programTempOwner.user);
  }

  protected loadRelationshipsOptions(): void {
    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.programTempOwner?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.trackedEntityInstanceService
      .query()
      .pipe(map((res: HttpResponse<ITrackedEntityInstance[]>) => res.body ?? []))
      .pipe(
        map((trackedEntityInstances: ITrackedEntityInstance[]) =>
          this.trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing<ITrackedEntityInstance>(
            trackedEntityInstances,
            this.programTempOwner?.entityInstance
          )
        )
      )
      .subscribe(
        (trackedEntityInstances: ITrackedEntityInstance[]) => (this.trackedEntityInstancesSharedCollection = trackedEntityInstances)
      );

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.programTempOwner?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
