import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFileResource, NewFileResource } from '../file-resource.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFileResource for edit and NewFileResourceFormGroupInput for create.
 */
type FileResourceFormGroupInput = IFileResource | PartialWithRequiredKeyOf<NewFileResource>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFileResource | NewFileResource> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type FileResourceFormRawValue = FormValueOf<IFileResource>;

type NewFileResourceFormRawValue = FormValueOf<NewFileResource>;

type FileResourceFormDefaults = Pick<NewFileResource, 'id' | 'created' | 'updated' | 'assigned' | 'hasMultipleStorageFiles'>;

type FileResourceFormGroupContent = {
  id: FormControl<FileResourceFormRawValue['id'] | NewFileResource['id']>;
  uid: FormControl<FileResourceFormRawValue['uid']>;
  code: FormControl<FileResourceFormRawValue['code']>;
  name: FormControl<FileResourceFormRawValue['name']>;
  created: FormControl<FileResourceFormRawValue['created']>;
  updated: FormControl<FileResourceFormRawValue['updated']>;
  contentType: FormControl<FileResourceFormRawValue['contentType']>;
  contentLength: FormControl<FileResourceFormRawValue['contentLength']>;
  contentMd5: FormControl<FileResourceFormRawValue['contentMd5']>;
  storageKey: FormControl<FileResourceFormRawValue['storageKey']>;
  assigned: FormControl<FileResourceFormRawValue['assigned']>;
  domain: FormControl<FileResourceFormRawValue['domain']>;
  hasMultipleStorageFiles: FormControl<FileResourceFormRawValue['hasMultipleStorageFiles']>;
  fileResourceOwner: FormControl<FileResourceFormRawValue['fileResourceOwner']>;
  createdBy: FormControl<FileResourceFormRawValue['createdBy']>;
  updatedBy: FormControl<FileResourceFormRawValue['updatedBy']>;
};

export type FileResourceFormGroup = FormGroup<FileResourceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FileResourceFormService {
  createFileResourceFormGroup(fileResource: FileResourceFormGroupInput = { id: null }): FileResourceFormGroup {
    const fileResourceRawValue = this.convertFileResourceToFileResourceRawValue({
      ...this.getFormDefaults(),
      ...fileResource,
    });
    return new FormGroup<FileResourceFormGroupContent>({
      id: new FormControl(
        { value: fileResourceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(fileResourceRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(fileResourceRawValue.code),
      name: new FormControl(fileResourceRawValue.name),
      created: new FormControl(fileResourceRawValue.created),
      updated: new FormControl(fileResourceRawValue.updated),
      contentType: new FormControl(fileResourceRawValue.contentType),
      contentLength: new FormControl(fileResourceRawValue.contentLength),
      contentMd5: new FormControl(fileResourceRawValue.contentMd5),
      storageKey: new FormControl(fileResourceRawValue.storageKey),
      assigned: new FormControl(fileResourceRawValue.assigned),
      domain: new FormControl(fileResourceRawValue.domain),
      hasMultipleStorageFiles: new FormControl(fileResourceRawValue.hasMultipleStorageFiles),
      fileResourceOwner: new FormControl(fileResourceRawValue.fileResourceOwner),
      createdBy: new FormControl(fileResourceRawValue.createdBy),
      updatedBy: new FormControl(fileResourceRawValue.updatedBy),
    });
  }

  getFileResource(form: FileResourceFormGroup): IFileResource | NewFileResource {
    return this.convertFileResourceRawValueToFileResource(form.getRawValue() as FileResourceFormRawValue | NewFileResourceFormRawValue);
  }

  resetForm(form: FileResourceFormGroup, fileResource: FileResourceFormGroupInput): void {
    const fileResourceRawValue = this.convertFileResourceToFileResourceRawValue({ ...this.getFormDefaults(), ...fileResource });
    form.reset(
      {
        ...fileResourceRawValue,
        id: { value: fileResourceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FileResourceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      assigned: false,
      hasMultipleStorageFiles: false,
    };
  }

  private convertFileResourceRawValueToFileResource(
    rawFileResource: FileResourceFormRawValue | NewFileResourceFormRawValue
  ): IFileResource | NewFileResource {
    return {
      ...rawFileResource,
      created: dayjs(rawFileResource.created, DATE_TIME_FORMAT),
      updated: dayjs(rawFileResource.updated, DATE_TIME_FORMAT),
    };
  }

  private convertFileResourceToFileResourceRawValue(
    fileResource: IFileResource | (Partial<NewFileResource> & FileResourceFormDefaults)
  ): FileResourceFormRawValue | PartialWithRequiredKeyOf<NewFileResourceFormRawValue> {
    return {
      ...fileResource,
      created: fileResource.created ? fileResource.created.format(DATE_TIME_FORMAT) : undefined,
      updated: fileResource.updated ? fileResource.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
