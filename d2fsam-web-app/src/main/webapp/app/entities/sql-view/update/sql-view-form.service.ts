import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISqlView, NewSqlView } from '../sql-view.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISqlView for edit and NewSqlViewFormGroupInput for create.
 */
type SqlViewFormGroupInput = ISqlView | PartialWithRequiredKeyOf<NewSqlView>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISqlView | NewSqlView> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type SqlViewFormRawValue = FormValueOf<ISqlView>;

type NewSqlViewFormRawValue = FormValueOf<NewSqlView>;

type SqlViewFormDefaults = Pick<NewSqlView, 'id' | 'created' | 'updated'>;

type SqlViewFormGroupContent = {
  id: FormControl<SqlViewFormRawValue['id'] | NewSqlView['id']>;
  uid: FormControl<SqlViewFormRawValue['uid']>;
  code: FormControl<SqlViewFormRawValue['code']>;
  name: FormControl<SqlViewFormRawValue['name']>;
  created: FormControl<SqlViewFormRawValue['created']>;
  updated: FormControl<SqlViewFormRawValue['updated']>;
  description: FormControl<SqlViewFormRawValue['description']>;
  sqlQuery: FormControl<SqlViewFormRawValue['sqlQuery']>;
  type: FormControl<SqlViewFormRawValue['type']>;
  cacheStrategy: FormControl<SqlViewFormRawValue['cacheStrategy']>;
  createdBy: FormControl<SqlViewFormRawValue['createdBy']>;
  updatedBy: FormControl<SqlViewFormRawValue['updatedBy']>;
};

export type SqlViewFormGroup = FormGroup<SqlViewFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SqlViewFormService {
  createSqlViewFormGroup(sqlView: SqlViewFormGroupInput = { id: null }): SqlViewFormGroup {
    const sqlViewRawValue = this.convertSqlViewToSqlViewRawValue({
      ...this.getFormDefaults(),
      ...sqlView,
    });
    return new FormGroup<SqlViewFormGroupContent>({
      id: new FormControl(
        { value: sqlViewRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(sqlViewRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(sqlViewRawValue.code),
      name: new FormControl(sqlViewRawValue.name, {
        validators: [Validators.required],
      }),
      created: new FormControl(sqlViewRawValue.created),
      updated: new FormControl(sqlViewRawValue.updated),
      description: new FormControl(sqlViewRawValue.description),
      sqlQuery: new FormControl(sqlViewRawValue.sqlQuery),
      type: new FormControl(sqlViewRawValue.type),
      cacheStrategy: new FormControl(sqlViewRawValue.cacheStrategy),
      createdBy: new FormControl(sqlViewRawValue.createdBy),
      updatedBy: new FormControl(sqlViewRawValue.updatedBy),
    });
  }

  getSqlView(form: SqlViewFormGroup): ISqlView | NewSqlView {
    return this.convertSqlViewRawValueToSqlView(form.getRawValue() as SqlViewFormRawValue | NewSqlViewFormRawValue);
  }

  resetForm(form: SqlViewFormGroup, sqlView: SqlViewFormGroupInput): void {
    const sqlViewRawValue = this.convertSqlViewToSqlViewRawValue({ ...this.getFormDefaults(), ...sqlView });
    form.reset(
      {
        ...sqlViewRawValue,
        id: { value: sqlViewRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SqlViewFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertSqlViewRawValueToSqlView(rawSqlView: SqlViewFormRawValue | NewSqlViewFormRawValue): ISqlView | NewSqlView {
    return {
      ...rawSqlView,
      created: dayjs(rawSqlView.created, DATE_TIME_FORMAT),
      updated: dayjs(rawSqlView.updated, DATE_TIME_FORMAT),
    };
  }

  private convertSqlViewToSqlViewRawValue(
    sqlView: ISqlView | (Partial<NewSqlView> & SqlViewFormDefaults)
  ): SqlViewFormRawValue | PartialWithRequiredKeyOf<NewSqlViewFormRawValue> {
    return {
      ...sqlView,
      created: sqlView.created ? sqlView.created.format(DATE_TIME_FORMAT) : undefined,
      updated: sqlView.updated ? sqlView.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
