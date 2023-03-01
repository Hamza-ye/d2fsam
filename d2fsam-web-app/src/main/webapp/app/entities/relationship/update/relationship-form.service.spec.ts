import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../relationship.test-samples';

import { RelationshipFormService } from './relationship-form.service';

describe('Relationship Form Service', () => {
  let service: RelationshipFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelationshipFormService);
  });

  describe('Service methods', () => {
    describe('createRelationshipFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelationshipFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            formName: expect.any(Object),
            description: expect.any(Object),
            key: expect.any(Object),
            invertedKey: expect.any(Object),
            deleted: expect.any(Object),
            relationshipType: expect.any(Object),
            from: expect.any(Object),
            to: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IRelationship should create a new form with FormGroup', () => {
        const formGroup = service.createRelationshipFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            formName: expect.any(Object),
            description: expect.any(Object),
            key: expect.any(Object),
            invertedKey: expect.any(Object),
            deleted: expect.any(Object),
            relationshipType: expect.any(Object),
            from: expect.any(Object),
            to: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getRelationship', () => {
      it('should return NewRelationship for default Relationship initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRelationshipFormGroup(sampleWithNewData);

        const relationship = service.getRelationship(formGroup) as any;

        expect(relationship).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelationship for empty Relationship initial value', () => {
        const formGroup = service.createRelationshipFormGroup();

        const relationship = service.getRelationship(formGroup) as any;

        expect(relationship).toMatchObject({});
      });

      it('should return IRelationship', () => {
        const formGroup = service.createRelationshipFormGroup(sampleWithRequiredData);

        const relationship = service.getRelationship(formGroup) as any;

        expect(relationship).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelationship should not enable id FormControl', () => {
        const formGroup = service.createRelationshipFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRelationship should disable id FormControl', () => {
        const formGroup = service.createRelationshipFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
