import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataElementFormService, DataElementFormGroup } from './data-element-form.service';
import { IDataElement } from '../data-element.model';
import { DataElementService } from '../service/data-element.service';
import { IOptionSet } from 'app/entities/option-set/option-set.model';
import { OptionSetService } from 'app/entities/option-set/service/option-set.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ValueType } from 'app/entities/enumerations/value-type.model';
import { AggregationType } from 'app/entities/enumerations/aggregation-type.model';

@Component({
  selector: 'app-data-element-update',
  templateUrl: './data-element-update.component.html',
})
export class DataElementUpdateComponent implements OnInit {
  isSaving = false;
  dataElement: IDataElement | null = null;
  valueTypeValues = Object.keys(ValueType);
  aggregationTypeValues = Object.keys(AggregationType);

  optionSetsSharedCollection: IOptionSet[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: DataElementFormGroup = this.dataElementFormService.createDataElementFormGroup();

  constructor(
    protected dataElementService: DataElementService,
    protected dataElementFormService: DataElementFormService,
    protected optionSetService: OptionSetService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOptionSet = (o1: IOptionSet | null, o2: IOptionSet | null): boolean => this.optionSetService.compareOptionSet(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dataElement }) => {
      this.dataElement = dataElement;
      if (dataElement) {
        this.updateForm(dataElement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dataElement = this.dataElementFormService.getDataElement(this.editForm);
    if (dataElement.id !== null) {
      this.subscribeToSaveResponse(this.dataElementService.update(dataElement));
    } else {
      this.subscribeToSaveResponse(this.dataElementService.create(dataElement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDataElement>>): void {
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

  protected updateForm(dataElement: IDataElement): void {
    this.dataElement = dataElement;
    this.dataElementFormService.resetForm(this.editForm, dataElement);

    this.optionSetsSharedCollection = this.optionSetService.addOptionSetToCollectionIfMissing<IOptionSet>(
      this.optionSetsSharedCollection,
      dataElement.optionSet
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      dataElement.createdBy,
      dataElement.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.optionSetService
      .query()
      .pipe(map((res: HttpResponse<IOptionSet[]>) => res.body ?? []))
      .pipe(
        map((optionSets: IOptionSet[]) =>
          this.optionSetService.addOptionSetToCollectionIfMissing<IOptionSet>(optionSets, this.dataElement?.optionSet)
        )
      )
      .subscribe((optionSets: IOptionSet[]) => (this.optionSetsSharedCollection = optionSets));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.dataElement?.createdBy, this.dataElement?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
