import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRelationshipItem, NewRelationshipItem } from '../relationship-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelationshipItem for edit and NewRelationshipItemFormGroupInput for create.
 */
type RelationshipItemFormGroupInput = IRelationshipItem | PartialWithRequiredKeyOf<NewRelationshipItem>;

type RelationshipItemFormDefaults = Pick<NewRelationshipItem, 'id'>;

type RelationshipItemFormGroupContent = {
  id: FormControl<IRelationshipItem['id'] | NewRelationshipItem['id']>;
  relationship: FormControl<IRelationshipItem['relationship']>;
  trackedEntityInstance: FormControl<IRelationshipItem['trackedEntityInstance']>;
  programInstance: FormControl<IRelationshipItem['programInstance']>;
  programStageInstance: FormControl<IRelationshipItem['programStageInstance']>;
};

export type RelationshipItemFormGroup = FormGroup<RelationshipItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelationshipItemFormService {
  createRelationshipItemFormGroup(relationshipItem: RelationshipItemFormGroupInput = { id: null }): RelationshipItemFormGroup {
    const relationshipItemRawValue = {
      ...this.getFormDefaults(),
      ...relationshipItem,
    };
    return new FormGroup<RelationshipItemFormGroupContent>({
      id: new FormControl(
        { value: relationshipItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      relationship: new FormControl(relationshipItemRawValue.relationship),
      trackedEntityInstance: new FormControl(relationshipItemRawValue.trackedEntityInstance),
      programInstance: new FormControl(relationshipItemRawValue.programInstance),
      programStageInstance: new FormControl(relationshipItemRawValue.programStageInstance),
    });
  }

  getRelationshipItem(form: RelationshipItemFormGroup): IRelationshipItem | NewRelationshipItem {
    return form.getRawValue() as IRelationshipItem | NewRelationshipItem;
  }

  resetForm(form: RelationshipItemFormGroup, relationshipItem: RelationshipItemFormGroupInput): void {
    const relationshipItemRawValue = { ...this.getFormDefaults(), ...relationshipItem };
    form.reset(
      {
        ...relationshipItemRawValue,
        id: { value: relationshipItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RelationshipItemFormDefaults {
    return {
      id: null,
    };
  }
}
