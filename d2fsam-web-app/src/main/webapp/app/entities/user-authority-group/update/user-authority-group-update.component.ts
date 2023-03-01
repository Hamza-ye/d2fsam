import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserAuthorityGroupFormService, UserAuthorityGroupFormGroup } from './user-authority-group-form.service';
import { IUserAuthorityGroup } from '../user-authority-group.model';
import { UserAuthorityGroupService } from '../service/user-authority-group.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'app-user-authority-group-update',
  templateUrl: './user-authority-group-update.component.html',
})
export class UserAuthorityGroupUpdateComponent implements OnInit {
  isSaving = false;
  userAuthorityGroup: IUserAuthorityGroup | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: UserAuthorityGroupFormGroup = this.userAuthorityGroupFormService.createUserAuthorityGroupFormGroup();

  constructor(
    protected userAuthorityGroupService: UserAuthorityGroupService,
    protected userAuthorityGroupFormService: UserAuthorityGroupFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAuthorityGroup }) => {
      this.userAuthorityGroup = userAuthorityGroup;
      if (userAuthorityGroup) {
        this.updateForm(userAuthorityGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userAuthorityGroup = this.userAuthorityGroupFormService.getUserAuthorityGroup(this.editForm);
    if (userAuthorityGroup.id !== null) {
      this.subscribeToSaveResponse(this.userAuthorityGroupService.update(userAuthorityGroup));
    } else {
      this.subscribeToSaveResponse(this.userAuthorityGroupService.create(userAuthorityGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserAuthorityGroup>>): void {
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

  protected updateForm(userAuthorityGroup: IUserAuthorityGroup): void {
    this.userAuthorityGroup = userAuthorityGroup;
    this.userAuthorityGroupFormService.resetForm(this.editForm, userAuthorityGroup);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      userAuthorityGroup.createdBy,
      userAuthorityGroup.updatedBy
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
            this.userAuthorityGroup?.createdBy,
            this.userAuthorityGroup?.updatedBy
          )
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
