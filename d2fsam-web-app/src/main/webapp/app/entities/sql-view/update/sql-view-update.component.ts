import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SqlViewFormService, SqlViewFormGroup } from './sql-view-form.service';
import { ISqlView } from '../sql-view.model';
import { SqlViewService } from '../service/sql-view.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { SqlViewType } from 'app/entities/enumerations/sql-view-type.model';
import { CacheStrategy } from 'app/entities/enumerations/cache-strategy.model';

@Component({
  selector: 'app-sql-view-update',
  templateUrl: './sql-view-update.component.html',
})
export class SqlViewUpdateComponent implements OnInit {
  isSaving = false;
  sqlView: ISqlView | null = null;
  sqlViewTypeValues = Object.keys(SqlViewType);
  cacheStrategyValues = Object.keys(CacheStrategy);

  usersSharedCollection: IUser[] = [];

  editForm: SqlViewFormGroup = this.sqlViewFormService.createSqlViewFormGroup();

  constructor(
    protected sqlViewService: SqlViewService,
    protected sqlViewFormService: SqlViewFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sqlView }) => {
      this.sqlView = sqlView;
      if (sqlView) {
        this.updateForm(sqlView);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sqlView = this.sqlViewFormService.getSqlView(this.editForm);
    if (sqlView.id !== null) {
      this.subscribeToSaveResponse(this.sqlViewService.update(sqlView));
    } else {
      this.subscribeToSaveResponse(this.sqlViewService.create(sqlView));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISqlView>>): void {
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

  protected updateForm(sqlView: ISqlView): void {
    this.sqlView = sqlView;
    this.sqlViewFormService.resetForm(this.editForm, sqlView);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      sqlView.createdBy,
      sqlView.updatedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.sqlView?.createdBy, this.sqlView?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
