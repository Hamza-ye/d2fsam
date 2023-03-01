import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISystemSetting, NewSystemSetting } from '../system-setting.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISystemSetting for edit and NewSystemSettingFormGroupInput for create.
 */
type SystemSettingFormGroupInput = ISystemSetting | PartialWithRequiredKeyOf<NewSystemSetting>;

type SystemSettingFormDefaults = Pick<NewSystemSetting, 'id'>;

type SystemSettingFormGroupContent = {
  id: FormControl<ISystemSetting['id'] | NewSystemSetting['id']>;
  name: FormControl<ISystemSetting['name']>;
  value: FormControl<ISystemSetting['value']>;
};

export type SystemSettingFormGroup = FormGroup<SystemSettingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SystemSettingFormService {
  createSystemSettingFormGroup(systemSetting: SystemSettingFormGroupInput = { id: null }): SystemSettingFormGroup {
    const systemSettingRawValue = {
      ...this.getFormDefaults(),
      ...systemSetting,
    };
    return new FormGroup<SystemSettingFormGroupContent>({
      id: new FormControl(
        { value: systemSettingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(systemSettingRawValue.name),
      value: new FormControl(systemSettingRawValue.value),
    });
  }

  getSystemSetting(form: SystemSettingFormGroup): ISystemSetting | NewSystemSetting {
    return form.getRawValue() as ISystemSetting | NewSystemSetting;
  }

  resetForm(form: SystemSettingFormGroup, systemSetting: SystemSettingFormGroupInput): void {
    const systemSettingRawValue = { ...this.getFormDefaults(), ...systemSetting };
    form.reset(
      {
        ...systemSettingRawValue,
        id: { value: systemSettingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SystemSettingFormDefaults {
    return {
      id: null,
    };
  }
}
