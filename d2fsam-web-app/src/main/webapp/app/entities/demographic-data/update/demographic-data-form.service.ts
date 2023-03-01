import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDemographicData, NewDemographicData } from '../demographic-data.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDemographicData for edit and NewDemographicDataFormGroupInput for create.
 */
type DemographicDataFormGroupInput = IDemographicData | PartialWithRequiredKeyOf<NewDemographicData>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDemographicData | NewDemographicData> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type DemographicDataFormRawValue = FormValueOf<IDemographicData>;

type NewDemographicDataFormRawValue = FormValueOf<NewDemographicData>;

type DemographicDataFormDefaults = Pick<NewDemographicData, 'id' | 'created' | 'updated'>;

type DemographicDataFormGroupContent = {
  id: FormControl<DemographicDataFormRawValue['id'] | NewDemographicData['id']>;
  uid: FormControl<DemographicDataFormRawValue['uid']>;
  code: FormControl<DemographicDataFormRawValue['code']>;
  created: FormControl<DemographicDataFormRawValue['created']>;
  updated: FormControl<DemographicDataFormRawValue['updated']>;
  date: FormControl<DemographicDataFormRawValue['date']>;
  level: FormControl<DemographicDataFormRawValue['level']>;
  totalPopulation: FormControl<DemographicDataFormRawValue['totalPopulation']>;
  malePopulation: FormControl<DemographicDataFormRawValue['malePopulation']>;
  femalePopulation: FormControl<DemographicDataFormRawValue['femalePopulation']>;
  lessThan5Population: FormControl<DemographicDataFormRawValue['lessThan5Population']>;
  greaterThan5Population: FormControl<DemographicDataFormRawValue['greaterThan5Population']>;
  bw5And15Population: FormControl<DemographicDataFormRawValue['bw5And15Population']>;
  greaterThan15Population: FormControl<DemographicDataFormRawValue['greaterThan15Population']>;
  households: FormControl<DemographicDataFormRawValue['households']>;
  houses: FormControl<DemographicDataFormRawValue['houses']>;
  healthFacilities: FormControl<DemographicDataFormRawValue['healthFacilities']>;
  avgNoOfRooms: FormControl<DemographicDataFormRawValue['avgNoOfRooms']>;
  avgRoomArea: FormControl<DemographicDataFormRawValue['avgRoomArea']>;
  avgHouseArea: FormControl<DemographicDataFormRawValue['avgHouseArea']>;
  individualsPerHousehold: FormControl<DemographicDataFormRawValue['individualsPerHousehold']>;
  populationGrowthRate: FormControl<DemographicDataFormRawValue['populationGrowthRate']>;
  comment: FormControl<DemographicDataFormRawValue['comment']>;
  organisationUnit: FormControl<DemographicDataFormRawValue['organisationUnit']>;
  createdBy: FormControl<DemographicDataFormRawValue['createdBy']>;
  updatedBy: FormControl<DemographicDataFormRawValue['updatedBy']>;
  source: FormControl<DemographicDataFormRawValue['source']>;
};

export type DemographicDataFormGroup = FormGroup<DemographicDataFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DemographicDataFormService {
  createDemographicDataFormGroup(demographicData: DemographicDataFormGroupInput = { id: null }): DemographicDataFormGroup {
    const demographicDataRawValue = this.convertDemographicDataToDemographicDataRawValue({
      ...this.getFormDefaults(),
      ...demographicData,
    });
    return new FormGroup<DemographicDataFormGroupContent>({
      id: new FormControl(
        { value: demographicDataRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(demographicDataRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(demographicDataRawValue.code),
      created: new FormControl(demographicDataRawValue.created),
      updated: new FormControl(demographicDataRawValue.updated),
      date: new FormControl(demographicDataRawValue.date, {
        validators: [Validators.required],
      }),
      level: new FormControl(demographicDataRawValue.level),
      totalPopulation: new FormControl(demographicDataRawValue.totalPopulation, {
        validators: [Validators.min(0)],
      }),
      malePopulation: new FormControl(demographicDataRawValue.malePopulation, {
        validators: [Validators.min(0)],
      }),
      femalePopulation: new FormControl(demographicDataRawValue.femalePopulation, {
        validators: [Validators.min(0)],
      }),
      lessThan5Population: new FormControl(demographicDataRawValue.lessThan5Population, {
        validators: [Validators.min(0)],
      }),
      greaterThan5Population: new FormControl(demographicDataRawValue.greaterThan5Population, {
        validators: [Validators.min(0)],
      }),
      bw5And15Population: new FormControl(demographicDataRawValue.bw5And15Population, {
        validators: [Validators.min(0)],
      }),
      greaterThan15Population: new FormControl(demographicDataRawValue.greaterThan15Population, {
        validators: [Validators.min(0)],
      }),
      households: new FormControl(demographicDataRawValue.households, {
        validators: [Validators.min(0)],
      }),
      houses: new FormControl(demographicDataRawValue.houses, {
        validators: [Validators.min(0)],
      }),
      healthFacilities: new FormControl(demographicDataRawValue.healthFacilities, {
        validators: [Validators.min(0)],
      }),
      avgNoOfRooms: new FormControl(demographicDataRawValue.avgNoOfRooms, {
        validators: [Validators.min(0.0)],
      }),
      avgRoomArea: new FormControl(demographicDataRawValue.avgRoomArea, {
        validators: [Validators.min(0.0)],
      }),
      avgHouseArea: new FormControl(demographicDataRawValue.avgHouseArea, {
        validators: [Validators.min(0.0)],
      }),
      individualsPerHousehold: new FormControl(demographicDataRawValue.individualsPerHousehold, {
        validators: [Validators.min(0.0)],
      }),
      populationGrowthRate: new FormControl(demographicDataRawValue.populationGrowthRate, {
        validators: [Validators.min(0.0)],
      }),
      comment: new FormControl(demographicDataRawValue.comment),
      organisationUnit: new FormControl(demographicDataRawValue.organisationUnit, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(demographicDataRawValue.createdBy),
      updatedBy: new FormControl(demographicDataRawValue.updatedBy),
      source: new FormControl(demographicDataRawValue.source, {
        validators: [Validators.required],
      }),
    });
  }

  getDemographicData(form: DemographicDataFormGroup): IDemographicData | NewDemographicData {
    return this.convertDemographicDataRawValueToDemographicData(
      form.getRawValue() as DemographicDataFormRawValue | NewDemographicDataFormRawValue
    );
  }

  resetForm(form: DemographicDataFormGroup, demographicData: DemographicDataFormGroupInput): void {
    const demographicDataRawValue = this.convertDemographicDataToDemographicDataRawValue({ ...this.getFormDefaults(), ...demographicData });
    form.reset(
      {
        ...demographicDataRawValue,
        id: { value: demographicDataRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DemographicDataFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
    };
  }

  private convertDemographicDataRawValueToDemographicData(
    rawDemographicData: DemographicDataFormRawValue | NewDemographicDataFormRawValue
  ): IDemographicData | NewDemographicData {
    return {
      ...rawDemographicData,
      created: dayjs(rawDemographicData.created, DATE_TIME_FORMAT),
      updated: dayjs(rawDemographicData.updated, DATE_TIME_FORMAT),
    };
  }

  private convertDemographicDataToDemographicDataRawValue(
    demographicData: IDemographicData | (Partial<NewDemographicData> & DemographicDataFormDefaults)
  ): DemographicDataFormRawValue | PartialWithRequiredKeyOf<NewDemographicDataFormRawValue> {
    return {
      ...demographicData,
      created: demographicData.created ? demographicData.created.format(DATE_TIME_FORMAT) : undefined,
      updated: demographicData.updated ? demographicData.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
