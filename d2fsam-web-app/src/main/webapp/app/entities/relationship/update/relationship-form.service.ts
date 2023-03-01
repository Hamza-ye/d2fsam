import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRelationship, NewRelationship } from '../relationship.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelationship for edit and NewRelationshipFormGroupInput for create.
 */
type RelationshipFormGroupInput = IRelationship | PartialWithRequiredKeyOf<NewRelationship>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRelationship | NewRelationship> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type RelationshipFormRawValue = FormValueOf<IRelationship>;

type NewRelationshipFormRawValue = FormValueOf<NewRelationship>;

type RelationshipFormDefaults = Pick<NewRelationship, 'id' | 'created' | 'updated' | 'deleted'>;

type RelationshipFormGroupContent = {
  id: FormControl<RelationshipFormRawValue['id'] | NewRelationship['id']>;
  uid: FormControl<RelationshipFormRawValue['uid']>;
  code: FormControl<RelationshipFormRawValue['code']>;
  created: FormControl<RelationshipFormRawValue['created']>;
  updated: FormControl<RelationshipFormRawValue['updated']>;
  formName: FormControl<RelationshipFormRawValue['formName']>;
  description: FormControl<RelationshipFormRawValue['description']>;
  key: FormControl<RelationshipFormRawValue['key']>;
  invertedKey: FormControl<RelationshipFormRawValue['invertedKey']>;
  deleted: FormControl<RelationshipFormRawValue['deleted']>;
  relationshipType: FormControl<RelationshipFormRawValue['relationshipType']>;
  from: FormControl<RelationshipFormRawValue['from']>;
  to: FormControl<RelationshipFormRawValue['to']>;
  updatedBy: FormControl<RelationshipFormRawValue['updatedBy']>;
};

export type RelationshipFormGroup = FormGroup<RelationshipFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelationshipFormService {
  createRelationshipFormGroup(relationship: RelationshipFormGroupInput = { id: null }): RelationshipFormGroup {
    const relationshipRawValue = this.convertRelationshipToRelationshipRawValue({
      ...this.getFormDefaults(),
      ...relationship,
    });
    return new FormGroup<RelationshipFormGroupContent>({
      id: new FormControl(
        { value: relationshipRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(relationshipRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(relationshipRawValue.code),
      created: new FormControl(relationshipRawValue.created),
      updated: new FormControl(relationshipRawValue.updated),
      formName: new FormControl(relationshipRawValue.formName),
      description: new FormControl(relationshipRawValue.description),
      key: new FormControl(relationshipRawValue.key),
      invertedKey: new FormControl(relationshipRawValue.invertedKey),
      deleted: new FormControl(relationshipRawValue.deleted),
      relationshipType: new FormControl(relationshipRawValue.relationshipType, {
        validators: [Validators.required],
      }),
      from: new FormControl(relationshipRawValue.from),
      to: new FormControl(relationshipRawValue.to),
      updatedBy: new FormControl(relationshipRawValue.updatedBy),
    });
  }

  getRelationship(form: RelationshipFormGroup): IRelationship | NewRelationship {
    return this.convertRelationshipRawValueToRelationship(form.getRawValue() as RelationshipFormRawValue | NewRelationshipFormRawValue);
  }

  resetForm(form: RelationshipFormGroup, relationship: RelationshipFormGroupInput): void {
    const relationshipRawValue = this.convertRelationshipToRelationshipRawValue({ ...this.getFormDefaults(), ...relationship });
    form.reset(
      {
        ...relationshipRawValue,
        id: { value: relationshipRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RelationshipFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      deleted: false,
    };
  }

  private convertRelationshipRawValueToRelationship(
    rawRelationship: RelationshipFormRawValue | NewRelationshipFormRawValue
  ): IRelationship | NewRelationship {
    return {
      ...rawRelationship,
      created: dayjs(rawRelationship.created, DATE_TIME_FORMAT),
      updated: dayjs(rawRelationship.updated, DATE_TIME_FORMAT),
    };
  }

  private convertRelationshipToRelationshipRawValue(
    relationship: IRelationship | (Partial<NewRelationship> & RelationshipFormDefaults)
  ): RelationshipFormRawValue | PartialWithRequiredKeyOf<NewRelationshipFormRawValue> {
    return {
      ...relationship,
      created: relationship.created ? relationship.created.format(DATE_TIME_FORMAT) : undefined,
      updated: relationship.updated ? relationship.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
