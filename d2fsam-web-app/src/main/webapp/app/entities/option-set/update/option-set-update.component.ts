import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OptionSetFormService, OptionSetFormGroup } from './option-set-form.service';
import { IOptionSet } from '../option-set.model';
import { OptionSetService } from '../service/option-set.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ValueType } from 'app/entities/enumerations/value-type.model';

@Component({
  selector: 'app-option-set-update',
  templateUrl: './option-set-update.component.html',
})
export class OptionSetUpdateComponent implements OnInit {
  isSaving = false;
  optionSet: IOptionSet | null = null;
  valueTypeValues = Object.keys(ValueType);

  usersSharedCollection: IUser[] = [];

  editForm: OptionSetFormGroup = this.optionSetFormService.createOptionSetFormGroup();

  constructor(
    protected optionSetService: OptionSetService,
    protected optionSetFormService: OptionSetFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ optionSet }) => {
      this.optionSet = optionSet;
      if (optionSet) {
        this.updateForm(optionSet);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const optionSet = this.optionSetFormService.getOptionSet(this.editForm);
    if (optionSet.id !== null) {
      this.subscribeToSaveResponse(this.optionSetService.update(optionSet));
    } else {
      this.subscribeToSaveResponse(this.optionSetService.create(optionSet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOptionSet>>): void {
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

  protected updateForm(optionSet: IOptionSet): void {
    this.optionSet = optionSet;
    this.optionSetFormService.resetForm(this.editForm, optionSet);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      optionSet.createdBy,
      optionSet.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.optionSet?.createdBy, this.optionSet?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
