import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDocument, NewDocument } from '../document.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDocument for edit and NewDocumentFormGroupInput for create.
 */
type DocumentFormGroupInput = IDocument | PartialWithRequiredKeyOf<NewDocument>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDocument | NewDocument> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type DocumentFormRawValue = FormValueOf<IDocument>;

type NewDocumentFormRawValue = FormValueOf<NewDocument>;

type DocumentFormDefaults = Pick<NewDocument, 'id' | 'created' | 'updated' | 'external' | 'attachment'>;

type DocumentFormGroupContent = {
  id: FormControl<DocumentFormRawValue['id'] | NewDocument['id']>;
  uid: FormControl<DocumentFormRawValue['uid']>;
  code: FormControl<DocumentFormRawValue['code']>;
  name: FormControl<DocumentFormRawValue['name']>;
  created: FormControl<DocumentFormRawValue['created']>;
  updated: FormControl<DocumentFormRawValue['updated']>;
  url: FormControl<DocumentFormRawValue['url']>;
  external: FormControl<DocumentFormRawValue['external']>;
  contentType: FormControl<DocumentFormRawValue['contentType']>;
  attachment: FormControl<DocumentFormRawValue['attachment']>;
  fileResource: FormControl<DocumentFormRawValue['fileResource']>;
  createdBy: FormControl<DocumentFormRawValue['createdBy']>;
  updatedBy: FormControl<DocumentFormRawValue['updatedBy']>;
};

export type DocumentFormGroup = FormGroup<DocumentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DocumentFormService {
  createDocumentFormGroup(document: DocumentFormGroupInput = { id: null }): DocumentFormGroup {
    const documentRawValue = this.convertDocumentToDocumentRawValue({
      ...this.getFormDefaults(),
      ...document,
    });
    return new FormGroup<DocumentFormGroupContent>({
      id: new FormControl(
        { value: documentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(documentRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(documentRawValue.code),
      name: new FormControl(documentRawValue.name),
      created: new FormControl(documentRawValue.created),
      updated: new FormControl(documentRawValue.updated),
      url: new FormControl(documentRawValue.url),
      external: new FormControl(documentRawValue.external),
      contentType: new FormControl(documentRawValue.contentType),
      attachment: new FormControl(documentRawValue.attachment),
      fileResource: new FormControl(documentRawValue.fileResource),
      createdBy: new FormControl(documentRawValue.createdBy),
      updatedBy: new FormControl(documentRawValue.updatedBy),
    });
  }

  getDocument(form: DocumentFormGroup): IDocument | NewDocument {
    return this.convertDocumentRawValueToDocument(form.getRawValue() as DocumentFormRawValue | NewDocumentFormRawValue);
  }

  resetForm(form: DocumentFormGroup, document: DocumentFormGroupInput): void {
    const documentRawValue = this.convertDocumentToDocumentRawValue({ ...this.getFormDefaults(), ...document });
    form.reset(
      {
        ...documentRawValue,
        id: { value: documentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DocumentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      external: false,
      attachment: false,
    };
  }

  private convertDocumentRawValueToDocument(rawDocument: DocumentFormRawValue | NewDocumentFormRawValue): IDocument | NewDocument {
    return {
      ...rawDocument,
      created: dayjs(rawDocument.created, DATE_TIME_FORMAT),
      updated: dayjs(rawDocument.updated, DATE_TIME_FORMAT),
    };
  }

  private convertDocumentToDocumentRawValue(
    document: IDocument | (Partial<NewDocument> & DocumentFormDefaults)
  ): DocumentFormRawValue | PartialWithRequiredKeyOf<NewDocumentFormRawValue> {
    return {
      ...document,
      created: document.created ? document.created.format(DATE_TIME_FORMAT) : undefined,
      updated: document.updated ? document.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
