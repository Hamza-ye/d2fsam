import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IChv, NewChv } from '../chv.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IChv for edit and NewChvFormGroupInput for create.
 */
type ChvFormGroupInput = IChv | PartialWithRequiredKeyOf<NewChv>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IChv | NewChv> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type ChvFormRawValue = FormValueOf<IChv>;

type NewChvFormRawValue = FormValueOf<NewChv>;

type ChvFormDefaults = Pick<NewChv, 'id' | 'created' | 'updated' | 'withdrawn' | 'inactive'>;

type ChvFormGroupContent = {
  id: FormControl<ChvFormRawValue['id'] | NewChv['id']>;
  uid: FormControl<ChvFormRawValue['uid']>;
  code: FormControl<ChvFormRawValue['code']>;
  created: FormControl<ChvFormRawValue['created']>;
  updated: FormControl<ChvFormRawValue['updated']>;
  withdrawn: FormControl<ChvFormRawValue['withdrawn']>;
  dateJoined: FormControl<ChvFormRawValue['dateJoined']>;
  dateWithdrawn: FormControl<ChvFormRawValue['dateWithdrawn']>;
  name: FormControl<ChvFormRawValue['name']>;
  description: FormControl<ChvFormRawValue['description']>;
  inactive: FormControl<ChvFormRawValue['inactive']>;
  assignedTo: FormControl<ChvFormRawValue['assignedTo']>;
  district: FormControl<ChvFormRawValue['district']>;
  homeSubvillage: FormControl<ChvFormRawValue['homeSubvillage']>;
  managingHf: FormControl<ChvFormRawValue['managingHf']>;
  createdBy: FormControl<ChvFormRawValue['createdBy']>;
  updatedBy: FormControl<ChvFormRawValue['updatedBy']>;
};

export type ChvFormGroup = FormGroup<ChvFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ChvFormService {
  createChvFormGroup(chv: ChvFormGroupInput = { id: null }): ChvFormGroup {
    const chvRawValue = this.convertChvToChvRawValue({
      ...this.getFormDefaults(),
      ...chv,
    });
    return new FormGroup<ChvFormGroupContent>({
      id: new FormControl(
        { value: chvRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(chvRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(chvRawValue.code),
      created: new FormControl(chvRawValue.created),
      updated: new FormControl(chvRawValue.updated),
      withdrawn: new FormControl(chvRawValue.withdrawn),
      dateJoined: new FormControl(chvRawValue.dateJoined),
      dateWithdrawn: new FormControl(chvRawValue.dateWithdrawn),
      name: new FormControl(chvRawValue.name),
      description: new FormControl(chvRawValue.description),
      inactive: new FormControl(chvRawValue.inactive),
      assignedTo: new FormControl(chvRawValue.assignedTo),
      district: new FormControl(chvRawValue.district),
      homeSubvillage: new FormControl(chvRawValue.homeSubvillage),
      managingHf: new FormControl(chvRawValue.managingHf),
      createdBy: new FormControl(chvRawValue.createdBy),
      updatedBy: new FormControl(chvRawValue.updatedBy),
    });
  }

  getChv(form: ChvFormGroup): IChv | NewChv {
    return this.convertChvRawValueToChv(form.getRawValue() as ChvFormRawValue | NewChvFormRawValue);
  }

  resetForm(form: ChvFormGroup, chv: ChvFormGroupInput): void {
    const chvRawValue = this.convertChvToChvRawValue({ ...this.getFormDefaults(), ...chv });
    form.reset(
      {
        ...chvRawValue,
        id: { value: chvRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ChvFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      withdrawn: false,
      inactive: false,
    };
  }

  private convertChvRawValueToChv(rawChv: ChvFormRawValue | NewChvFormRawValue): IChv | NewChv {
    return {
      ...rawChv,
      created: dayjs(rawChv.created, DATE_TIME_FORMAT),
      updated: dayjs(rawChv.updated, DATE_TIME_FORMAT),
    };
  }

  private convertChvToChvRawValue(
    chv: IChv | (Partial<NewChv> & ChvFormDefaults)
  ): ChvFormRawValue | PartialWithRequiredKeyOf<NewChvFormRawValue> {
    return {
      ...chv,
      created: chv.created ? chv.created.format(DATE_TIME_FORMAT) : undefined,
      updated: chv.updated ? chv.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
