import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import {
  ProgramTrackedEntityAttributeFormService,
  ProgramTrackedEntityAttributeFormGroup,
} from './program-tracked-entity-attribute-form.service';
import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';
import { ProgramTrackedEntityAttributeService } from '../service/program-tracked-entity-attribute.service';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-program-tracked-entity-attribute-update',
  templateUrl: './program-tracked-entity-attribute-update.component.html',
})
export class ProgramTrackedEntityAttributeUpdateComponent implements OnInit {
  isSaving = false;
  programTrackedEntityAttribute: IProgramTrackedEntityAttribute | null = null;

  programsSharedCollection: IProgram[] = [];
  dataElementsSharedCollection: IDataElement[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: ProgramTrackedEntityAttributeFormGroup =
    this.programTrackedEntityAttributeFormService.createProgramTrackedEntityAttributeFormGroup();

  constructor(
    protected programTrackedEntityAttributeService: ProgramTrackedEntityAttributeService,
    protected programTrackedEntityAttributeFormService: ProgramTrackedEntityAttributeFormService,
    protected programService: ProgramService,
    protected dataElementService: DataElementService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProgram = (o1: IProgram | null, o2: IProgram | null): boolean => this.programService.compareProgram(o1, o2);

  compareDataElement = (o1: IDataElement | null, o2: IDataElement | null): boolean => this.dataElementService.compareDataElement(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programTrackedEntityAttribute }) => {
      this.programTrackedEntityAttribute = programTrackedEntityAttribute;
      if (programTrackedEntityAttribute) {
        this.updateForm(programTrackedEntityAttribute);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const programTrackedEntityAttribute = this.programTrackedEntityAttributeFormService.getProgramTrackedEntityAttribute(this.editForm);
    if (programTrackedEntityAttribute.id !== null) {
      this.subscribeToSaveResponse(this.programTrackedEntityAttributeService.update(programTrackedEntityAttribute));
    } else {
      this.subscribeToSaveResponse(this.programTrackedEntityAttributeService.create(programTrackedEntityAttribute));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgramTrackedEntityAttribute>>): void {
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

  protected updateForm(programTrackedEntityAttribute: IProgramTrackedEntityAttribute): void {
    this.programTrackedEntityAttribute = programTrackedEntityAttribute;
    this.programTrackedEntityAttributeFormService.resetForm(this.editForm, programTrackedEntityAttribute);

    this.programsSharedCollection = this.programService.addProgramToCollectionIfMissing<IProgram>(
      this.programsSharedCollection,
      programTrackedEntityAttribute.program
    );
    this.dataElementsSharedCollection = this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
      this.dataElementsSharedCollection,
      programTrackedEntityAttribute.attribute
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      programTrackedEntityAttribute.createdBy,
      programTrackedEntityAttribute.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.programService
      .query()
      .pipe(map((res: HttpResponse<IProgram[]>) => res.body ?? []))
      .pipe(
        map((programs: IProgram[]) =>
          this.programService.addProgramToCollectionIfMissing<IProgram>(programs, this.programTrackedEntityAttribute?.program)
        )
      )
      .subscribe((programs: IProgram[]) => (this.programsSharedCollection = programs));

    this.dataElementService
      .query()
      .pipe(map((res: HttpResponse<IDataElement[]>) => res.body ?? []))
      .pipe(
        map((dataElements: IDataElement[]) =>
          this.dataElementService.addDataElementToCollectionIfMissing<IDataElement>(
            dataElements,
            this.programTrackedEntityAttribute?.attribute
          )
        )
      )
      .subscribe((dataElements: IDataElement[]) => (this.dataElementsSharedCollection = dataElements));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(
            users,
            this.programTrackedEntityAttribute?.createdBy,
            this.programTrackedEntityAttribute?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
