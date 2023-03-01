import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../relationship-type.test-samples';

import { RelationshipTypeFormService } from './relationship-type-form.service';

describe('RelationshipType Form Service', () => {
  let service: RelationshipTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelationshipTypeFormService);
  });

  describe('Service methods', () => {
    describe('createRelationshipTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelationshipTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            bidirectional: expect.any(Object),
            fromToName: expect.any(Object),
            toFromName: expect.any(Object),
            referral: expect.any(Object),
            fromConstraint: expect.any(Object),
            toConstraint: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IRelationshipType should create a new form with FormGroup', () => {
        const formGroup = service.createRelationshipTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            bidirectional: expect.any(Object),
            fromToName: expect.any(Object),
            toFromName: expect.any(Object),
            referral: expect.any(Object),
            fromConstraint: expect.any(Object),
            toConstraint: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getRelationshipType', () => {
      it('should return NewRelationshipType for default RelationshipType initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRelationshipTypeFormGroup(sampleWithNewData);

        const relationshipType = service.getRelationshipType(formGroup) as any;

        expect(relationshipType).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelationshipType for empty RelationshipType initial value', () => {
        const formGroup = service.createRelationshipTypeFormGroup();

        const relationshipType = service.getRelationshipType(formGroup) as any;

        expect(relationshipType).toMatchObject({});
      });

      it('should return IRelationshipType', () => {
        const formGroup = service.createRelationshipTypeFormGroup(sampleWithRequiredData);

        const relationshipType = service.getRelationshipType(formGroup) as any;

        expect(relationshipType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelationshipType should not enable id FormControl', () => {
        const formGroup = service.createRelationshipTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRelationshipType should disable id FormControl', () => {
        const formGroup = service.createRelationshipTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
