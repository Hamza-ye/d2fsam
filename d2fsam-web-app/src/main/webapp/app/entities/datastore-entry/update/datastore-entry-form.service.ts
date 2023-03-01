import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDatastoreEntry, NewDatastoreEntry } from '../datastore-entry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDatastoreEntry for edit and NewDatastoreEntryFormGroupInput for create.
 */
type DatastoreEntryFormGroupInput = IDatastoreEntry | PartialWithRequiredKeyOf<NewDatastoreEntry>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDatastoreEntry | NewDatastoreEntry> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type DatastoreEntryFormRawValue = FormValueOf<IDatastoreEntry>;

type NewDatastoreEntryFormRawValue = FormValueOf<NewDatastoreEntry>;

type DatastoreEntryFormDefaults = Pick<NewDatastoreEntry, 'id' | 'created' | 'updated' | 'encrypted'>;

type DatastoreEntryFormGroupContent = {
  id: FormControl<DatastoreEntryFormRawValue['id'] | NewDatastoreEntry['id']>;
  uid: FormControl<DatastoreEntryFormRawValue['uid']>;
  code: FormControl<DatastoreEntryFormRawValue['code']>;
  created: FormControl<DatastoreEntryFormRawValue['created']>;
  updated: FormControl<DatastoreEntryFormRawValue['updated']>;
  key: FormControl<DatastoreEntryFormRawValue['key']>;
  namespace: FormControl<DatastoreEntryFormRawValue['namespace']>;
  encrypted: FormControl<DatastoreEntryFormRawValue['encrypted']>;
  createdBy: FormControl<DatastoreEntryFormRawValue['createdBy']>;
  updatedBy: FormControl<DatastoreEntryFormRawValue['updatedBy']>;
};

export type DatastoreEntryFormGroup = FormGroup<DatastoreEntryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DatastoreEntryFormService {
  createDatastoreEntryFormGroup(datastoreEntry: DatastoreEntryFormGroupInput = { id: null }): DatastoreEntryFormGroup {
    const datastoreEntryRawValue = this.convertDatastoreEntryToDatastoreEntryRawValue({
      ...this.getFormDefaults(),
      ...datastoreEntry,
    });
    return new FormGroup<DatastoreEntryFormGroupContent>({
      id: new FormControl(
        { value: datastoreEntryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(datastoreEntryRawValue.uid, {
        validators: [Validators.maxLength(11)],
      }),
      code: new FormControl(datastoreEntryRawValue.code),
      created: new FormControl(datastoreEntryRawValue.created),
      updated: new FormControl(datastoreEntryRawValue.updated),
      key: new FormControl(datastoreEntryRawValue.key),
      namespace: new FormControl(datastoreEntryRawValue.namespace),
      encrypted: new FormControl(datastoreEntryRawValue.encrypted),
      createdBy: new FormControl(datastoreEntryRawValue.createdBy),
      updatedBy: new FormControl(datastoreEntryRawValue.updatedBy),
    });
  }

  getDatastoreEntry(form: DatastoreEntryFormGroup): IDatastoreEntry | NewDatastoreEntry {
    return this.convertDatastoreEntryRawValueToDatastoreEntry(
      form.getRawValue() as DatastoreEntryFormRawValue | NewDatastoreEntryFormRawValue
    );
  }

  resetForm(form: DatastoreEntryFormGroup, datastoreEntry: DatastoreEntryFormGroupInput): void {
    const datastoreEntryRawValue = this.convertDatastoreEntryToDatastoreEntryRawValue({ ...this.getFormDefaults(), ...datastoreEntry });
    form.reset(
      {
        ...datastoreEntryRawValue,
        id: { value: datastoreEntryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DatastoreEntryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      encrypted: false,
    };
  }

  private convertDatastoreEntryRawValueToDatastoreEntry(
    rawDatastoreEntry: DatastoreEntryFormRawValue | NewDatastoreEntryFormRawValue
  ): IDatastoreEntry | NewDatastoreEntry {
    return {
      ...rawDatastoreEntry,
      created: dayjs(rawDatastoreEntry.created, DATE_TIME_FORMAT),
      updated: dayjs(rawDatastoreEntry.updated, DATE_TIME_FORMAT),
    };
  }

  private convertDatastoreEntryToDatastoreEntryRawValue(
    datastoreEntry: IDatastoreEntry | (Partial<NewDatastoreEntry> & DatastoreEntryFormDefaults)
  ): DatastoreEntryFormRawValue | PartialWithRequiredKeyOf<NewDatastoreEntryFormRawValue> {
    return {
      ...datastoreEntry,
      created: datastoreEntry.created ? datastoreEntry.created.format(DATE_TIME_FORMAT) : undefined,
      updated: datastoreEntry.updated ? datastoreEntry.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
