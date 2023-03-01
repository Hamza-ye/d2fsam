import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRelationshipConstraint, NewRelationshipConstraint } from '../relationship-constraint.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelationshipConstraint for edit and NewRelationshipConstraintFormGroupInput for create.
 */
type RelationshipConstraintFormGroupInput = IRelationshipConstraint | PartialWithRequiredKeyOf<NewRelationshipConstraint>;

type RelationshipConstraintFormDefaults = Pick<NewRelationshipConstraint, 'id'>;

type RelationshipConstraintFormGroupContent = {
  id: FormControl<IRelationshipConstraint['id'] | NewRelationshipConstraint['id']>;
  relationshipEntity: FormControl<IRelationshipConstraint['relationshipEntity']>;
  trackedEntityType: FormControl<IRelationshipConstraint['trackedEntityType']>;
  program: FormControl<IRelationshipConstraint['program']>;
  programStage: FormControl<IRelationshipConstraint['programStage']>;
};

export type RelationshipConstraintFormGroup = FormGroup<RelationshipConstraintFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelationshipConstraintFormService {
  createRelationshipConstraintFormGroup(
    relationshipConstraint: RelationshipConstraintFormGroupInput = { id: null }
  ): RelationshipConstraintFormGroup {
    const relationshipConstraintRawValue = {
      ...this.getFormDefaults(),
      ...relationshipConstraint,
    };
    return new FormGroup<RelationshipConstraintFormGroupContent>({
      id: new FormControl(
        { value: relationshipConstraintRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      relationshipEntity: new FormControl(relationshipConstraintRawValue.relationshipEntity),
      trackedEntityType: new FormControl(relationshipConstraintRawValue.trackedEntityType),
      program: new FormControl(relationshipConstraintRawValue.program),
      programStage: new FormControl(relationshipConstraintRawValue.programStage),
    });
  }

  getRelationshipConstraint(form: RelationshipConstraintFormGroup): IRelationshipConstraint | NewRelationshipConstraint {
    return form.getRawValue() as IRelationshipConstraint | NewRelationshipConstraint;
  }

  resetForm(form: RelationshipConstraintFormGroup, relationshipConstraint: RelationshipConstraintFormGroupInput): void {
    const relationshipConstraintRawValue = { ...this.getFormDefaults(), ...relationshipConstraint };
    form.reset(
      {
        ...relationshipConstraintRawValue,
        id: { value: relationshipConstraintRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RelationshipConstraintFormDefaults {
    return {
      id: null,
    };
  }
}
