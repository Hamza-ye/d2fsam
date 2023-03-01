import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OptionFormService, OptionFormGroup } from './option-form.service';
import { IOption } from '../option.model';
import { OptionService } from '../service/option.service';
import { IOptionSet } from 'app/entities/option-set/option-set.model';
import { OptionSetService } from 'app/entities/option-set/service/option-set.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-option-update',
  templateUrl: './option-update.component.html',
})
export class OptionUpdateComponent implements OnInit {
  isSaving = false;
  option: IOption | null = null;

  optionSetsSharedCollection: IOptionSet[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: OptionFormGroup = this.optionFormService.createOptionFormGroup();

  constructor(
    protected optionService: OptionService,
    protected optionFormService: OptionFormService,
    protected optionSetService: OptionSetService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOptionSet = (o1: IOptionSet | null, o2: IOptionSet | null): boolean => this.optionSetService.compareOptionSet(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ option }) => {
      this.option = option;
      if (option) {
        this.updateForm(option);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const option = this.optionFormService.getOption(this.editForm);
    if (option.id !== null) {
      this.subscribeToSaveResponse(this.optionService.update(option));
    } else {
      this.subscribeToSaveResponse(this.optionService.create(option));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOption>>): void {
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

  protected updateForm(option: IOption): void {
    this.option = option;
    this.optionFormService.resetForm(this.editForm, option);

    this.optionSetsSharedCollection = this.optionSetService.addOptionSetToCollectionIfMissing<IOptionSet>(
      this.optionSetsSharedCollection,
      option.optionSet
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      option.createdBy,
      option.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.optionSetService
      .query()
      .pipe(map((res: HttpResponse<IOptionSet[]>) => res.body ?? []))
      .pipe(
        map((optionSets: IOptionSet[]) =>
          this.optionSetService.addOptionSetToCollectionIfMissing<IOptionSet>(optionSets, this.option?.optionSet)
        )
      )
      .subscribe((optionSets: IOptionSet[]) => (this.optionSetsSharedCollection = optionSets));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.option?.createdBy, this.option?.updatedBy))
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
