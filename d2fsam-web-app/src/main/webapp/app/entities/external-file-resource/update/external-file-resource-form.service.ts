import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IExternalFileResource, NewExternalFileResource } from '../external-file-resource.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExternalFileResource for edit and NewExternalFileResourceFormGroupInput for create.
 */
type ExternalFileResourceFormGroupInput = IExternalFileResource | PartialWithRequiredKeyOf<NewExternalFileResource>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IExternalFileResource | NewExternalFileResource> = Omit<T, 'created' | 'updated' | 'expires'> & {
  created?: string | null;
  updated?: string | null;
  expires?: string | null;
};

type ExternalFileResourceFormRawValue = FormValueOf<IExternalFileResource>;

type NewExternalFileResourceFormRawValue = FormValueOf<NewExternalFileResource>;

type ExternalFileResourceFormDefaults = Pick<NewExternalFileResource, 'id' | 'created' | 'updated' | 'expires'>;

type ExternalFileResourceFormGroupContent = {
  id: FormControl<ExternalFileResourceFormRawValue['id'] | NewExternalFileResource['id']>;
  uid: FormControl<ExternalFileResourceFormRawValue['uid']>;
  code: FormControl<ExternalFileResourceFormRawValue['code']>;
  name: FormControl<ExternalFileResourceFormRawValue['name']>;
  created: FormControl<ExternalFileResourceFormRawValue['created']>;
  updated: FormControl<ExternalFileResourceFormRawValue['updated']>;
  accessToken: FormControl<ExternalFileResourceFormRawValue['accessToken']>;
  expires: FormControl<ExternalFileResourceFormRawValue['expires']>;
  fileResource: FormControl<ExternalFileResourceFormRawValue['fileResource']>;
  createdBy: FormControl<ExternalFileResourceFormRawValue['createdBy']>;
  updatedBy: FormControl<ExternalFileResourceFormRawValue['updatedBy']>;
};

export type ExternalFileResourceFormGroup = FormGroup<ExternalFileResourceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExternalFileResourceFormService {
  createExternalFileResourceFormGroup(
    externalFileResource: ExternalFileResourceFormGroupInput = { id: null }
  ): ExternalFileResourceFormGroup {
    const externalFileResourceRawValue = this.convertExternalFileResourceToExternalFileResourceRawValue({
      ...this.getFormDefaults(),
      ...externalFileResource,
    });
    return new FormGroup<ExternalFileResourceFormGroupContent>({
      id: new FormControl(
        { value: externalFileResourceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(externalFileResourceRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(externalFileResourceRawValue.code),
      name: new FormControl(externalFileResourceRawValue.name),
      created: new FormControl(externalFileResourceRawValue.created),
      updated: new FormControl(externalFileResourceRawValue.updated),
      accessToken: new FormControl(externalFileResourceRawValue.accessToken),
      expires: new FormControl(externalFileResourceRawValue.expires),
      fileResource: new FormControl(externalFileResourceRawValue.fileResource),
      createdBy: new FormControl(externalFileResourceRawValue.createdBy),
      updatedBy: new FormControl(externalFileResourceRawValue.updatedBy),
    });
  }

  getExternalFileResource(form: ExternalFileResourceFormGroup): IExternalFileResource | NewExternalFileResource {
    return this.convertExternalFileResourceRawValueToExternalFileResource(
      form.getRawValue() as ExternalFileResourceFormRawValue | NewExternalFileResourceFormRawValue
    );
  }

  resetForm(form: ExternalFileResourceFormGroup, externalFileResource: ExternalFileResourceFormGroupInput): void {
    const externalFileResourceRawValue = this.convertExternalFileResourceToExternalFileResourceRawValue({
      ...this.getFormDefaults(),
      ...externalFileResource,
    });
    form.reset(
      {
        ...externalFileResourceRawValue,
        id: { value: externalFileResourceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ExternalFileResourceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      expires: currentTime,
    };
  }

  private convertExternalFileResourceRawValueToExternalFileResource(
    rawExternalFileResource: ExternalFileResourceFormRawValue | NewExternalFileResourceFormRawValue
  ): IExternalFileResource | NewExternalFileResource {
    return {
      ...rawExternalFileResource,
      created: dayjs(rawExternalFileResource.created, DATE_TIME_FORMAT),
      updated: dayjs(rawExternalFileResource.updated, DATE_TIME_FORMAT),
      expires: dayjs(rawExternalFileResource.expires, DATE_TIME_FORMAT),
    };
  }

  private convertExternalFileResourceToExternalFileResourceRawValue(
    externalFileResource: IExternalFileResource | (Partial<NewExternalFileResource> & ExternalFileResourceFormDefaults)
  ): ExternalFileResourceFormRawValue | PartialWithRequiredKeyOf<NewExternalFileResourceFormRawValue> {
    return {
      ...externalFileResource,
      created: externalFileResource.created ? externalFileResource.created.format(DATE_TIME_FORMAT) : undefined,
      updated: externalFileResource.updated ? externalFileResource.updated.format(DATE_TIME_FORMAT) : undefined,
      expires: externalFileResource.expires ? externalFileResource.expires.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
