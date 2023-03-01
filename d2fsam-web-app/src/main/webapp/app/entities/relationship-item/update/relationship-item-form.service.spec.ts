import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../relationship-item.test-samples';

import { RelationshipItemFormService } from './relationship-item-form.service';

describe('RelationshipItem Form Service', () => {
  let service: RelationshipItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelationshipItemFormService);
  });

  describe('Service methods', () => {
    describe('createRelationshipItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelationshipItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            relationship: expect.any(Object),
            trackedEntityInstance: expect.any(Object),
            programInstance: expect.any(Object),
            programStageInstance: expect.any(Object),
          })
        );
      });

      it('passing IRelationshipItem should create a new form with FormGroup', () => {
        const formGroup = service.createRelationshipItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            relationship: expect.any(Object),
            trackedEntityInstance: expect.any(Object),
            programInstance: expect.any(Object),
            programStageInstance: expect.any(Object),
          })
        );
      });
    });

    describe('getRelationshipItem', () => {
      it('should return NewRelationshipItem for default RelationshipItem initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRelationshipItemFormGroup(sampleWithNewData);

        const relationshipItem = service.getRelationshipItem(formGroup) as any;

        expect(relationshipItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelationshipItem for empty RelationshipItem initial value', () => {
        const formGroup = service.createRelationshipItemFormGroup();

        const relationshipItem = service.getRelationshipItem(formGroup) as any;

        expect(relationshipItem).toMatchObject({});
      });

      it('should return IRelationshipItem', () => {
        const formGroup = service.createRelationshipItemFormGroup(sampleWithRequiredData);

        const relationshipItem = service.getRelationshipItem(formGroup) as any;

        expect(relationshipItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelationshipItem should not enable id FormControl', () => {
        const formGroup = service.createRelationshipItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRelationshipItem should disable id FormControl', () => {
        const formGroup = service.createRelationshipItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
