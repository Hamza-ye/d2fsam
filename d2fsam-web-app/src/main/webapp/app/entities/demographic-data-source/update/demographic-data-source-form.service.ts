import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDemographicDataSource, NewDemographicDataSource } from '../demographic-data-source.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDemographicDataSource for edit and NewDemographicDataSourceFormGroupInput for create.
 */
type DemographicDataSourceFormGroupInput = IDemographicDataSource | PartialWithRequiredKeyOf<NewDemographicDataSource>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDemographicDataSource | NewDemographicDataSource> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type DemographicDataSourceFormRawValue = FormValueOf<IDemographicDataSource>;

type NewDemographicDataSourceFormRawValue = FormValueOf<NewDemographicDataSource>;

type DemographicDataSourceFormDefaults = Pick<NewDemographicDataSource, 'id' | 'created' | 'updated'>;

type DemographicDataSourceFormGroupContent = {
  id: FormControl<DemographicDataSourceFormRawValue['id'] | NewDemographicDataSource['id']>;
  uid: FormControl<DemographicDataSourceFormRawValue['uid']>;
  code: FormControl<DemographicDataSourceFormRawValue['code']>;
  name: FormControl<DemographicDataSourceFormRawValue['name']>;
  created: FormControl<DemographicDataSourceFormRawValue['created']>;
  updated: FormControl<DemographicDataSourceFormRawValue['updated']>;
  createdBy: FormControl<DemographicDataSourceFormRawValue['createdBy']>;
  updatedBy: FormControl<DemographicDataSourceFormRawValue['updatedBy']>;
};

export type DemographicDataSourceFormGroup = FormGroup<DemographicDataSourceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DemographicDataSourceFormService {
  createDemographicDataSourceFormGroup(
    demographicDataSource: DemographicDataSourceFormGroupInput = { id: null }
  ): DemographicDataSourceFormGroup {
    const demographicDataSourceRawValue = this.convertDemographicDataSourceToDemographicDataSourceRawValue({
      ...this.getFormDefaults(),
      ...demographicDataSource,
    });
    return new FormGroup<DemographicDataSourceFormGroupContent>({
      id: new FormControl(
        { value: demographicDataSourceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(demographicDataSourceRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(demographicDataSourceRawValue.code),
      name: new FormControl(demographicDataSourceRawValue.name, {
        validators: [Validators.required],
      }),
      created: new FormControl(demographicDataSourceRawValue.created),
      updated: new FormControl(demographicDataSourceRawValue.updated),
      createdBy: new FormControl(demographicDataSourceRawValue.createdBy),
      updatedBy: new FormControl(demographicDataSourceRawValue.updatedBy),
    });
  }

  getDemographicDataSource(form: DemographicDataSourceFormGroup): IDemographicDataSource | NewDemographicDataSource {
    return this.convertDemographicDataSourceRawValueToDemographicDataSource(
      form.getRawValue() as DemographicDataSourceFormRawValue | NewDemographicDataSourceFormRawValue
    );
  }

  resetForm(form: DemographicDataSourceFormGroup, demographicDataSource: DemographicDataSourceFormGroupInput): void {
    const demographicDataSourceRawValue = this.convertDemographicDataSourceToDemographicDataSourceRawValue({
      ...this.getFormDefaults(),
      ...demographicDataSource,
    });
    form.reset(
      {
        ...demographicDataSourceRawValue,
        id: { value: demographicDataSourceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DemographicDataSourceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertDemographicDataSourceRawValueToDemographicDataSource(
    rawDemographicDataSource: DemographicDataSourceFormRawValue | NewDemographicDataSourceFormRawValue
  ): IDemographicDataSource | NewDemographicDataSource {
    return {
      ...rawDemographicDataSource,
      created: dayjs(rawDemographicDataSource.created, DATE_TIME_FORMAT),
      updated: dayjs(rawDemographicDataSource.updated, DATE_TIME_FORMAT),
    };
  }

  private convertDemographicDataSourceToDemographicDataSourceRawValue(
    demographicDataSource: IDemographicDataSource | (Partial<NewDemographicDataSource> & DemographicDataSourceFormDefaults)
  ): DemographicDataSourceFormRawValue | PartialWithRequiredKeyOf<NewDemographicDataSourceFormRawValue> {
    return {
      ...demographicDataSource,
      created: demographicDataSource.created ? demographicDataSource.created.format(DATE_TIME_FORMAT) : undefined,
      updated: demographicDataSource.updated ? demographicDataSource.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
