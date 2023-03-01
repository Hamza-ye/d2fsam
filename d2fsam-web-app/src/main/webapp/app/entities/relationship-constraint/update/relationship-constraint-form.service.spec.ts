import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../relationship-constraint.test-samples';

import { RelationshipConstraintFormService } from './relationship-constraint-form.service';

describe('RelationshipConstraint Form Service', () => {
  let service: RelationshipConstraintFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelationshipConstraintFormService);
  });

  describe('Service methods', () => {
    describe('createRelationshipConstraintFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelationshipConstraintFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            relationshipEntity: expect.any(Object),
            trackedEntityType: expect.any(Object),
            program: expect.any(Object),
            programStage: expect.any(Object),
          })
        );
      });

      it('passing IRelationshipConstraint should create a new form with FormGroup', () => {
        const formGroup = service.createRelationshipConstraintFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            relationshipEntity: expect.any(Object),
            trackedEntityType: expect.any(Object),
            program: expect.any(Object),
            programStage: expect.any(Object),
          })
        );
      });
    });

    describe('getRelationshipConstraint', () => {
      it('should return NewRelationshipConstraint for default RelationshipConstraint initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRelationshipConstraintFormGroup(sampleWithNewData);

        const relationshipConstraint = service.getRelationshipConstraint(formGroup) as any;

        expect(relationshipConstraint).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelationshipConstraint for empty RelationshipConstraint initial value', () => {
        const formGroup = service.createRelationshipConstraintFormGroup();

        const relationshipConstraint = service.getRelationshipConstraint(formGroup) as any;

        expect(relationshipConstraint).toMatchObject({});
      });

      it('should return IRelationshipConstraint', () => {
        const formGroup = service.createRelationshipConstraintFormGroup(sampleWithRequiredData);

        const relationshipConstraint = service.getRelationshipConstraint(formGroup) as any;

        expect(relationshipConstraint).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelationshipConstraint should not enable id FormControl', () => {
        const formGroup = service.createRelationshipConstraintFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRelationshipConstraint should disable id FormControl', () => {
        const formGroup = service.createRelationshipConstraintFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
