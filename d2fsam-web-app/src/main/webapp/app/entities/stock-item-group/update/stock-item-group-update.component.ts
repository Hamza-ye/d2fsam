import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StockItemGroupFormService, StockItemGroupFormGroup } from './stock-item-group-form.service';
import { IStockItemGroup } from '../stock-item-group.model';
import { StockItemGroupService } from '../service/stock-item-group.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IStockItem } from 'app/entities/stock-item/stock-item.model';
import { StockItemService } from 'app/entities/stock-item/service/stock-item.service';

@Component({
  selector: 'app-stock-item-group-update',
  templateUrl: './stock-item-group-update.component.html',
})
export class StockItemGroupUpdateComponent implements OnInit {
  isSaving = false;
  stockItemGroup: IStockItemGroup | null = null;

  usersSharedCollection: IUser[] = [];
  stockItemsSharedCollection: IStockItem[] = [];

  editForm: StockItemGroupFormGroup = this.stockItemGroupFormService.createStockItemGroupFormGroup();

  constructor(
    protected stockItemGroupService: StockItemGroupService,
    protected stockItemGroupFormService: StockItemGroupFormService,
    protected userService: UserService,
    protected stockItemService: StockItemService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareStockItem = (o1: IStockItem | null, o2: IStockItem | null): boolean => this.stockItemService.compareStockItem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockItemGroup }) => {
      this.stockItemGroup = stockItemGroup;
      if (stockItemGroup) {
        this.updateForm(stockItemGroup);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stockItemGroup = this.stockItemGroupFormService.getStockItemGroup(this.editForm);
    if (stockItemGroup.id !== null) {
      this.subscribeToSaveResponse(this.stockItemGroupService.update(stockItemGroup));
    } else {
      this.subscribeToSaveResponse(this.stockItemGroupService.create(stockItemGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStockItemGroup>>): void {
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

  protected updateForm(stockItemGroup: IStockItemGroup): void {
    this.stockItemGroup = stockItemGroup;
    this.stockItemGroupFormService.resetForm(this.editForm, stockItemGroup);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      stockItemGroup.createdBy,
      stockItemGroup.updatedBy
    );
    this.stockItemsSharedCollection = this.stockItemService.addStockItemToCollectionIfMissing<IStockItem>(
      this.stockItemsSharedCollection,
      ...(stockItemGroup.items ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) =>
          this.userService.addUserToCollectionIfMissing<IUser>(users, this.stockItemGroup?.createdBy, this.stockItemGroup?.updatedBy)
        )
      )
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.stockItemService
      .query()
      .pipe(map((res: HttpResponse<IStockItem[]>) => res.body ?? []))
      .pipe(
        map((stockItems: IStockItem[]) =>
          this.stockItemService.addStockItemToCollectionIfMissing<IStockItem>(stockItems, ...(this.stockItemGroup?.items ?? []))
        )
      )
      .subscribe((stockItems: IStockItem[]) => (this.stockItemsSharedCollection = stockItems));
  }
}
