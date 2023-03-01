import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserGroupFormService, UserGroupFormGroup } from './user-group-form.service';
import { IUserGroup } from '../user-group.model';
import { UserGroupService } from '../service/user-group.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { UserDataService } from 'app/entities/user-data/service/user-data.service';

@Component({
  selector: 'app-user-group-update',
  templateUrl: './user-group-update.component.html',
})
export class UserGroupUpdateComponent implements OnInit {
  isSaving = false;
  userGroup: IUserGroup | null = null;

  usersSharedCollection: IUser[] = [];
  userDataSharedCollection: IUserData[] = [];
  userGroupsSharedCollection: IUserGroup[] = [];

  editForm: UserGroupFormGroup = this.userGroupFormService.createUserGroupFormGroup();

  constructor(
    protected userGroupService: UserGroupService,
    protected userGroupFormService: UserGroupFormService,
    protected userService: UserService,
    protected userDataService: UserDataService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareUserData = (o1: IUserData | null, o2: IUserData | null): boolean => this.userDataService.compareUserData(o1, o2);

  compareUserGroup = (o1: IUserGroup | null, o2: IUserGroup | null): boolean => this.userGroupService.compareUserGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userGroup }) => {
      this.userGroup = userGroup;
      if (userGroup) {
        this.updateForm(userGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userGroup = this.userGroupFormService.getUserGroup(this.editForm);
    if (userGroup.id !== null) {
      this.subscribeToSaveResponse(this.userGroupService.update(userGroup));
    } else {
      this.subscribeToSaveResponse(this.userGroupService.create(userGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserGroup>>): void {
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

  protected updateForm(userGroup: IUserGroup): void {
    this.userGroup = userGroup;
    this.userGroupFormService.resetForm(this.editForm, userGroup);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      userGroup.createdBy,
      userGroup.updatedBy
    );
    this.userDataSharedCollection = this.userDataService.addUserDataToCollectionIfMissing<IUserData>(
      this.userDataSharedCollection,
      ...(userGroup.members ?? [])
    );
    this.userGroupsSharedCollection = this.userGroupService.addUserGroupToCollectionIfMissing<IUserGroup>(
      this.userGroupsSharedCollection,
      ...(userGroup.managedGroups ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.userGroup?.createdBy, this.userGroup?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.userDataService
      .query()
      .pipe(map((res: HttpResponse<IUserData[]>) => res.body ?? []))
      .pipe(
        map((userData: IUserData[]) =>
          this.userDataService.addUserDataToCollectionIfMissing<IUserData>(userData, ...(this.userGroup?.members ?? []))
        )
      )
      .subscribe((userData: IUserData[]) => (this.userDataSharedCollection = userData));

    this.userGroupService
      .query()
      .pipe(map((res: HttpResponse<IUserGroup[]>) => res.body ?? []))
      .pipe(
        map((userGroups: IUserGroup[]) =>
          this.userGroupService.addUserGroupToCollectionIfMissing<IUserGroup>(userGroups, ...(this.userGroup?.managedGroups ?? []))
        )
      )
      .subscribe((userGroups: IUserGroup[]) => (this.userGroupsSharedCollection = userGroups));
  }
}
