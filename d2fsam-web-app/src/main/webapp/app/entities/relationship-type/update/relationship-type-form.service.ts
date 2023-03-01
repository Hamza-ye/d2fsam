import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRelationshipType, NewRelationshipType } from '../relationship-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelationshipType for edit and NewRelationshipTypeFormGroupInput for create.
 */
type RelationshipTypeFormGroupInput = IRelationshipType | PartialWithRequiredKeyOf<NewRelationshipType>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRelationshipType | NewRelationshipType> = Omit<T, 'created' | 'updated'> & {
  created?: string | null;
  updated?: string | null;
};

type RelationshipTypeFormRawValue = FormValueOf<IRelationshipType>;

type NewRelationshipTypeFormRawValue = FormValueOf<NewRelationshipType>;

type RelationshipTypeFormDefaults = Pick<NewRelationshipType, 'id' | 'created' | 'updated' | 'bidirectional' | 'referral'>;

type RelationshipTypeFormGroupContent = {
  id: FormControl<RelationshipTypeFormRawValue['id'] | NewRelationshipType['id']>;
  uid: FormControl<RelationshipTypeFormRawValue['uid']>;
  code: FormControl<RelationshipTypeFormRawValue['code']>;
  name: FormControl<RelationshipTypeFormRawValue['name']>;
  created: FormControl<RelationshipTypeFormRawValue['created']>;
  updated: FormControl<RelationshipTypeFormRawValue['updated']>;
  description: FormControl<RelationshipTypeFormRawValue['description']>;
  bidirectional: FormControl<RelationshipTypeFormRawValue['bidirectional']>;
  fromToName: FormControl<RelationshipTypeFormRawValue['fromToName']>;
  toFromName: FormControl<RelationshipTypeFormRawValue['toFromName']>;
  referral: FormControl<RelationshipTypeFormRawValue['referral']>;
  fromConstraint: FormControl<RelationshipTypeFormRawValue['fromConstraint']>;
  toConstraint: FormControl<RelationshipTypeFormRawValue['toConstraint']>;
  createdBy: FormControl<RelationshipTypeFormRawValue['createdBy']>;
  updatedBy: FormControl<RelationshipTypeFormRawValue['updatedBy']>;
};

export type RelationshipTypeFormGroup = FormGroup<RelationshipTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelationshipTypeFormService {
  createRelationshipTypeFormGroup(relationshipType: RelationshipTypeFormGroupInput = { id: null }): RelationshipTypeFormGroup {
    const relationshipTypeRawValue = this.convertRelationshipTypeToRelationshipTypeRawValue({
      ...this.getFormDefaults(),
      ...relationshipType,
    });
    return new FormGroup<RelationshipTypeFormGroupContent>({
      id: new FormControl(
        { value: relationshipTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      uid: new FormControl(relationshipTypeRawValue.uid, {
        validators: [Validators.required, Validators.maxLength(11)],
      }),
      code: new FormControl(relationshipTypeRawValue.code),
      name: new FormControl(relationshipTypeRawValue.name, {
        validators: [Validators.required],
      }),
      created: new FormControl(relationshipTypeRawValue.created),
      updated: new FormControl(relationshipTypeRawValue.updated),
      description: new FormControl(relationshipTypeRawValue.description),
      bidirectional: new FormControl(relationshipTypeRawValue.bidirectional),
      fromToName: new FormControl(relationshipTypeRawValue.fromToName),
      toFromName: new FormControl(relationshipTypeRawValue.toFromName),
      referral: new FormControl(relationshipTypeRawValue.referral),
      fromConstraint: new FormControl(relationshipTypeRawValue.fromConstraint),
      toConstraint: new FormControl(relationshipTypeRawValue.toConstraint),
      createdBy: new FormControl(relationshipTypeRawValue.createdBy),
      updatedBy: new FormControl(relationshipTypeRawValue.updatedBy),
    });
  }

  getRelationshipType(form: RelationshipTypeFormGroup): IRelationshipType | NewRelationshipType {
    return this.convertRelationshipTypeRawValueToRelationshipType(
      form.getRawValue() as RelationshipTypeFormRawValue | NewRelationshipTypeFormRawValue
    );
  }

  resetForm(form: RelationshipTypeFormGroup, relationshipType: RelationshipTypeFormGroupInput): void {
    const relationshipTypeRawValue = this.convertRelationshipTypeToRelationshipTypeRawValue({
      ...this.getFormDefaults(),
      ...relationshipType,
    });
    form.reset(
      {
        ...relationshipTypeRawValue,
        id: { value: relationshipTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RelationshipTypeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      updated: currentTime,
      bidirectional: false,
      referral: false,
    };
  }

  private convertRelationshipTypeRawValueToRelationshipType(
    rawRelationshipType: RelationshipTypeFormRawValue | NewRelationshipTypeFormRawValue
  ): IRelationshipType | NewRelationshipType {
    return {
      ...rawRelationshipType,
      created: dayjs(rawRelationshipType.created, DATE_TIME_FORMAT),
      updated: dayjs(rawRelationshipType.updated, DATE_TIME_FORMAT),
    };
  }

  private convertRelationshipTypeToRelationshipTypeRawValue(
    relationshipType: IRelationshipType | (Partial<NewRelationshipType> & RelationshipTypeFormDefaults)
  ): RelationshipTypeFormRawValue | PartialWithRequiredKeyOf<NewRelationshipTypeFormRawValue> {
    return {
      ...relationshipType,
      created: relationshipType.created ? relationshipType.created.format(DATE_TIME_FORMAT) : undefined,
      updated: relationshipType.updated ? relationshipType.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
