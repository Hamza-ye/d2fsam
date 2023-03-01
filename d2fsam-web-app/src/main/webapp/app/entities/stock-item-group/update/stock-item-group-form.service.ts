import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStockItemGroup, NewStockItemGroup } from '../stock-item-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockItemGroup for edit and NewStockItemGroupFormGroupInput for create.
 */
type StockItemGroupFormGroupInput = IStockItemGroup | PartialWithRequiredKeyOf<NewStockItemGroup>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockItemGroup | NewStockItemGroup> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type StockItemGroupFormRawValue = FormValueOf<IStockItemGroup>;

type NewStockItemGroupFormRawValue = FormValueOf<NewStockItemGroup>;

type StockItemGroupFormDefaults = Pick<NewStockItemGroup, 'id' | 'created' | 'updated' | 'inactive' | 'items'>;

type StockItemGroupFormGroupContent = {
  id: FormControl<StockItemGroupFormRawValue['id'] | NewStockItemGroup['id']>;
  uid: FormControl<StockItemGroupFormRawValue['uid']>;
  code: FormControl<StockItemGroupFormRawValue['code']>;
  name: FormControl<StockItemGroupFormRawValue['name']>;
  shortName: FormControl<StockItemGroupFormRawValue['shortName']>;
  description: FormControl<StockItemGroupFormRawValue['description']>;
  created: FormControl<StockItemGroupFormRawValue['created']>;
  updated: FormControl<StockItemGroupFormRawValue['updated']>;
  inactive: FormControl<StockItemGroupFormRawValue['inactive']>;
  createdBy: FormControl<StockItemGroupFormRawValue['createdBy']>;
  updatedBy: FormControl<StockItemGroupFormRawValue['updatedBy']>;
  items: FormControl<StockItemGroupFormRawValue['items']>;
};

export type StockItemGroupFormGroup = FormGroup<StockItemGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockItemGroupFormService {
  createStockItemGroupFormGroup(stockItemGroup: StockItemGroupFormGroupInput = { id: null }): StockItemGroupFormGroup {
    const stockItemGroupRawValue = this.convertStockItemGroupToStockItemGroupRawValue({
      ...this.getFormDefaults(),
      ...stockItemGroup,
    });
    return new FormGroup<StockItemGroupFormGroupContent>({
      id: new FormControl(
        { value: stockItemGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(stockItemGroupRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(stockItemGroupRawValue.code),
      name: new FormControl(stockItemGroupRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(stockItemGroupRawValue.shortName, {
        validators: [Validators.maxLength(50)],
      }),
      description: new FormControl(stockItemGroupRawValue.description),
      created: new FormControl(stockItemGroupRawValue.created),
      updated: new FormControl(stockItemGroupRawValue.updated),
      inactive: new FormControl(stockItemGroupRawValue.inactive),
      createdBy: new FormControl(stockItemGroupRawValue.createdBy),
      updatedBy: new FormControl(stockItemGroupRawValue.updatedBy),
      items: new FormControl(stockItemGroupRawValue.items ?? []),
    });
  }

  getStockItemGroup(form: StockItemGroupFormGroup): IStockItemGroup | NewStockItemGroup {
    return this.convertStockItemGroupRawValueToStockItemGroup(
      form.getRawValue() as StockItemGroupFormRawValue | NewStockItemGroupFormRawValue
    );
  }

  resetForm(form: StockItemGroupFormGroup, stockItemGroup: StockItemGroupFormGroupInput): void {
    const stockItemGroupRawValue = this.convertStockItemGroupToStockItemGroupRawValue({ ...this.getFormDefaults(), ...stockItemGroup });
    form.reset(
      {
        ...stockItemGroupRawValue,
        id: { value: stockItemGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StockItemGroupFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      inactive: false,
      items: [],
    };
  }

  private convertStockItemGroupRawValueToStockItemGroup(
    rawStockItemGroup: StockItemGroupFormRawValue | NewStockItemGroupFormRawValue
  ): IStockItemGroup | NewStockItemGroup {
    return {
      ...rawStockItemGroup,
      created: dayjs(rawStockItemGroup.created, DATE_TIME_FORMAT),
      updated: dayjs(rawStockItemGroup.updated, DATE_TIME_FORMAT),
    };
  }

  private convertStockItemGroupToStockItemGroupRawValue(
    stockItemGroup: IStockItemGroup | (Partial<NewStockItemGroup> & StockItemGroupFormDefaults)
  ): StockItemGroupFormRawValue | PartialWithRequiredKeyOf<NewStockItemGroupFormRawValue> {
    return {
      ...stockItemGroup,
      created: stockItemGroup.created ? stockItemGroup.created.format(DATE_TIME_FORMAT) : undefined,
      updated: stockItemGroup.updated ? stockItemGroup.updated.format(DATE_TIME_FORMAT) : undefined,
      items: stockItemGroup.items ?? [],
    };
  }
}
