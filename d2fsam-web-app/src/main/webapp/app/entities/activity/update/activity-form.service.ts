import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IActivity, NewActivity } from '../activity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IActivity for edit and NewActivityFormGroupInput for create.
 */
type ActivityFormGroupInput = IActivity | PartialWithRequiredKeyOf<NewActivity>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IActivity | NewActivity> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ActivityFormRawValue = FormValueOf<IActivity>;

type NewActivityFormRawValue = FormValueOf<NewActivity>;

type ActivityFormDefaults = Pick<NewActivity, 'id' | 'created' | 'updated' | 'hidden' | 'inactive' | 'targetedOrganisationUnits'>;

type ActivityFormGroupContent = {
  id: FormControl<ActivityFormRawValue['id'] | NewActivity['id']>;
  uid: FormControl<ActivityFormRawValue['uid']>;
  code: FormControl<ActivityFormRawValue['code']>;
  name: FormControl<ActivityFormRawValue['name']>;
  shortName: FormControl<ActivityFormRawValue['shortName']>;
  description: FormControl<ActivityFormRawValue['description']>;
  created: FormControl<ActivityFormRawValue['created']>;
  updated: FormControl<ActivityFormRawValue['updated']>;
  startDate: FormControl<ActivityFormRawValue['startDate']>;
  endDate: FormControl<ActivityFormRawValue['endDate']>;
  hidden: FormControl<ActivityFormRawValue['hidden']>;
  order: FormControl<ActivityFormRawValue['order']>;
  inactive: FormControl<ActivityFormRawValue['inactive']>;
  project: FormControl<ActivityFormRawValue['project']>;
  activityUsedAsTarget: FormControl<ActivityFormRawValue['activityUsedAsTarget']>;
  demographicData: FormControl<ActivityFormRawValue['demographicData']>;
  createdBy: FormControl<ActivityFormRawValue['createdBy']>;
  updatedBy: FormControl<ActivityFormRawValue['updatedBy']>;
  targetedOrganisationUnits: FormControl<ActivityFormRawValue['targetedOrganisationUnits']>;
};

export type ActivityFormGroup = FormGroup<ActivityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ActivityFormService {
  createActivityFormGroup(activity: ActivityFormGroupInput = { id: null }): ActivityFormGroup {
    const activityRawValue = this.convertActivityToActivityRawValue({
      ...this.getFormDefaults(),
      ...activity,
    });
    return new FormGroup<ActivityFormGroupContent>({
      id: new FormControl(
        { value: activityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(activityRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(activityRawValue.code),
      name: new FormControl(activityRawValue.name, {
        validators: [Validators.required],
      }),
      shortName: new FormControl(activityRawValue.shortName),
      description: new FormControl(activityRawValue.description),
      created: new FormControl(activityRawValue.created),
      updated: new FormControl(activityRawValue.updated),
      startDate: new FormControl(activityRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(activityRawValue.endDate, {
        validators: [Validators.required],
      }),
      hidden: new FormControl(activityRawValue.hidden),
      order: new FormControl(activityRawValue.order),
      inactive: new FormControl(activityRawValue.inactive),
      project: new FormControl(activityRawValue.project, {
        validators: [Validators.required],
      }),
      activityUsedAsTarget: new FormControl(activityRawValue.activityUsedAsTarget),
      demographicData: new FormControl(activityRawValue.demographicData),
      createdBy: new FormControl(activityRawValue.createdBy),
      updatedBy: new FormControl(activityRawValue.updatedBy),
      targetedOrganisationUnits: new FormControl(activityRawValue.targetedOrganisationUnits ?? []),
    });
  }

  getActivity(form: ActivityFormGroup): IActivity | NewActivity {
    return this.convertActivityRawValueToActivity(form.getRawValue() as ActivityFormRawValue | NewActivityFormRawValue);
  }

  resetForm(form: ActivityFormGroup, activity: ActivityFormGroupInput): void {
    const activityRawValue = this.convertActivityToActivityRawValue({ ...this.getFormDefaults(), ...activity });
    form.reset(
      {
        ...activityRawValue,
        id: { value: activityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ActivityFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      hidden: false,
      inactive: false,
      targetedOrganisationUnits: [],
    };
  }

  private convertActivityRawValueToActivity(rawActivity: ActivityFormRawValue | NewActivityFormRawValue): IActivity | NewActivity {
    return {
      ...rawActivity,
      created: dayjs(rawActivity.created, DATE_TIME_FORMAT),
      updated: dayjs(rawActivity.updated, DATE_TIME_FORMAT),
    };
  }

  private convertActivityToActivityRawValue(
    activity: IActivity | (Partial<NewActivity> & ActivityFormDefaults)
  ): ActivityFormRawValue | PartialWithRequiredKeyOf<NewActivityFormRawValue> {
    return {
      ...activity,
      created: activity.created ? activity.created.format(DATE_TIME_FORMAT) : undefined,
      updated: activity.updated ? activity.updated.format(DATE_TIME_FORMAT) : undefined,
      targetedOrganisationUnits: activity.targetedOrganisationUnits ?? [],
    };
  }
}
