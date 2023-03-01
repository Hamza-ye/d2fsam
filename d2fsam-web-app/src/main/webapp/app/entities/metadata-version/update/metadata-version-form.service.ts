import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMetadataVersion, NewMetadataVersion } from '../metadata-version.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMetadataVersion for edit and NewMetadataVersionFormGroupInput for create.
 */
type MetadataVersionFormGroupInput = IMetadataVersion | PartialWithRequiredKeyOf<NewMetadataVersion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMetadataVersion | NewMetadataVersion> = Omit<T, 'created' | 'updated' | 'importDate'> & {
  created?: string | null;
  updated?: string | null;
  importDate?: string | null;
};

type MetadataVersionFormRawValue = FormValueOf<IMetadataVersion>;

type NewMetadataVersionFormRawValue = FormValueOf<NewMetadataVersion>;

type MetadataVersionFormDefaults = Pick<NewMetadataVersion, 'id' | 'created' | 'updated' | 'importDate'>;

type MetadataVersionFormGroupContent = {
  id: FormControl<MetadataVersionFormRawValue['id'] | NewMetadataVersion['id']>;
  uid: FormControl<MetadataVersionFormRawValue['uid']>;
  code: FormControl<MetadataVersionFormRawValue['code']>;
  created: FormControl<MetadataVersionFormRawValue['created']>;
  updated: FormControl<MetadataVersionFormRawValue['updated']>;
  importDate: FormControl<MetadataVersionFormRawValue['importDate']>;
  type: FormControl<MetadataVersionFormRawValue['type']>;
  hashCode: FormControl<MetadataVersionFormRawValue['hashCode']>;
  updatedBy: FormControl<MetadataVersionFormRawValue['updatedBy']>;
};

export type MetadataVersionFormGroup = FormGroup<MetadataVersionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MetadataVersionFormService {
  createMetadataVersionFormGroup(metadataVersion: MetadataVersionFormGroupInput = { id: null }): MetadataVersionFormGroup {
    const metadataVersionRawValue = this.convertMetadataVersionToMetadataVersionRawValue({
      ...this.getFormDefaults(),
      ...metadataVersion,
    });
    return new FormGroup<MetadataVersionFormGroupContent>({
      id: new FormControl(
        { value: metadataVersionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(metadataVersionRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(metadataVersionRawValue.code),
      created: new FormControl(metadataVersionRawValue.created),
      updated: new FormControl(metadataVersionRawValue.updated),
      importDate: new FormControl(metadataVersionRawValue.importDate),
      type: new FormControl(metadataVersionRawValue.type),
      hashCode: new FormControl(metadataVersionRawValue.hashCode),
      updatedBy: new FormControl(metadataVersionRawValue.updatedBy),
    });
  }

  getMetadataVersion(form: MetadataVersionFormGroup): IMetadataVersion | NewMetadataVersion {
    return this.convertMetadataVersionRawValueToMetadataVersion(
      form.getRawValue() as MetadataVersionFormRawValue | NewMetadataVersionFormRawValue
    );
  }

  resetForm(form: MetadataVersionFormGroup, metadataVersion: MetadataVersionFormGroupInput): void {
    const metadataVersionRawValue = this.convertMetadataVersionToMetadataVersionRawValue({ ...this.getFormDefaults(), ...metadataVersion });
    form.reset(
      {
        ...metadataVersionRawValue,
        id: { value: metadataVersionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MetadataVersionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      importDate: currentTime,
    };
  }

  private convertMetadataVersionRawValueToMetadataVersion(
    rawMetadataVersion: MetadataVersionFormRawValue | NewMetadataVersionFormRawValue
  ): IMetadataVersion | NewMetadataVersion {
    return {
      ...rawMetadataVersion,
      created: dayjs(rawMetadataVersion.created, DATE_TIME_FORMAT),
      updated: dayjs(rawMetadataVersion.updated, DATE_TIME_FORMAT),
      importDate: dayjs(rawMetadataVersion.importDate, DATE_TIME_FORMAT),
    };
  }

  private convertMetadataVersionToMetadataVersionRawValue(
    metadataVersion: IMetadataVersion | (Partial<NewMetadataVersion> & MetadataVersionFormDefaults)
  ): MetadataVersionFormRawValue | PartialWithRequiredKeyOf<NewMetadataVersionFormRawValue> {
    return {
      ...metadataVersion,
      created: metadataVersion.created ? metadataVersion.created.format(DATE_TIME_FORMAT) : undefined,
      updated: metadataVersion.updated ? metadataVersion.updated.format(DATE_TIME_FORMAT) : undefined,
      importDate: metadataVersion.importDate ? metadataVersion.importDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
