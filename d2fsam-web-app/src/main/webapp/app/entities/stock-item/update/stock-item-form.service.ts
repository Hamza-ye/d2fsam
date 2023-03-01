import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStockItem, NewStockItem } from '../stock-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockItem for edit and NewStockItemFormGroupInput for create.
 */
type StockItemFormGroupInput = IStockItem | PartialWithRequiredKeyOf<NewStockItem>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockItem | NewStockItem> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type StockItemFormRawValue = FormValueOf<IStockItem>;

type NewStockItemFormRawValue = FormValueOf<NewStockItem>;

type StockItemFormDefaults = Pick<NewStockItem, 'id' | 'created' | 'updated' | 'inactive' | 'groups'>;

type StockItemFormGroupContent = {
  id: FormControl<StockItemFormRawValue['id'] | NewStockItem['id']>;
  uid: FormControl<StockItemFormRawValue['uid']>;
  code: FormControl<StockItemFormRawValue['code']>;
  name: FormControl<StockItemFormRawValue['name']>;
  shortName: FormControl<StockItemFormRawValue['shortName']>;
  description: FormControl<StockItemFormRawValue['description']>;
  created: FormControl<StockItemFormRawValue['created']>;
  updated: FormControl<StockItemFormRawValue['updated']>;
  inactive: FormControl<StockItemFormRawValue['inactive']>;
  createdBy: FormControl<StockItemFormRawValue['createdBy']>;
  updatedBy: FormControl<StockItemFormRawValue['updatedBy']>;
  groups: FormControl<StockItemFormRawValue['groups']>;
};

export type StockItemFormGroup = FormGroup<StockItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockItemFormService {
  createStockItemFormGroup(stockItem: StockItemFormGroupInput = { id: null }): StockItemFormGroup {
    const stockItemRawValue = this.convertStockItemToStockItemRawValue({
      ...this.getFormDefaults(),
      ...stockItem,
    });
    return new FormGroup<StockItemFormGroupContent>({
      id: new FormControl(
        { value: stockItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(stockItemRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(stockItemRawValue.code),
      name: new FormControl(stockItemRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(stockItemRawValue.shortName, {
        validators: [Validators.maxLength(50)],
      }),
      description: new FormControl(stockItemRawValue.description),
      created: new FormControl(stockItemRawValue.created),
      updated: new FormControl(stockItemRawValue.updated),
      inactive: new FormControl(stockItemRawValue.inactive),
      createdBy: new FormControl(stockItemRawValue.createdBy),
      updatedBy: new FormControl(stockItemRawValue.updatedBy),
      groups: new FormControl(stockItemRawValue.groups ?? []),
    });
  }

  getStockItem(form: StockItemFormGroup): IStockItem | NewStockItem {
    return this.convertStockItemRawValueToStockItem(form.getRawValue() as StockItemFormRawValue | NewStockItemFormRawValue);
  }

  resetForm(form: StockItemFormGroup, stockItem: StockItemFormGroupInput): void {
    const stockItemRawValue = this.convertStockItemToStockItemRawValue({ ...this.getFormDefaults(), ...stockItem });
    form.reset(
      {
        ...stockItemRawValue,
        id: { value: stockItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StockItemFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      groups: [],
    };
  }

  private convertStockItemRawValueToStockItem(rawStockItem: StockItemFormRawValue | NewStockItemFormRawValue): IStockItem | NewStockItem {
    return {
      ...rawStockItem,
      created: dayjs(rawStockItem.created, DATE_TIME_FORMAT),
      updated: dayjs(rawStockItem.updated, DATE_TIME_FORMAT),
    };
  }

  private convertStockItemToStockItemRawValue(
    stockItem: IStockItem | (Partial<NewStockItem> & StockItemFormDefaults)
  ): StockItemFormRawValue | PartialWithRequiredKeyOf<NewStockItemFormRawValue> {
    return {
      ...stockItem,
      created: stockItem.created ? stockItem.created.format(DATE_TIME_FORMAT) : undefined,
      updated: stockItem.updated ? stockItem.updated.format(DATE_TIME_FORMAT) : undefined,
      groups: stockItem.groups ?? [],
    };
  }
}
