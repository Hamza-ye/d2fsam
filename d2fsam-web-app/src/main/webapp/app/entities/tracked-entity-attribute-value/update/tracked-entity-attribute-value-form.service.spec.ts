import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-attribute-value.test-samples';

import { TrackedEntityAttributeValueFormService } from './tracked-entity-attribute-value-form.service';

describe('TrackedEntityAttributeValue Form Service', () => {
  let service: TrackedEntityAttributeValueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityAttributeValueFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityAttributeValueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityAttributeValueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            encryptedValue: expect.any(Object),
            plainValue: expect.any(Object),
            value: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            storedBy: expect.any(Object),
            attribute: expect.any(Object),
            entityInstance: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityAttributeValue should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityAttributeValueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            encryptedValue: expect.any(Object),
            plainValue: expect.any(Object),
            value: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            storedBy: expect.any(Object),
            attribute: expect.any(Object),
            entityInstance: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityAttributeValue', () => {
      it('should return NewTrackedEntityAttributeValue for default TrackedEntityAttributeValue initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityAttributeValueFormGroup(sampleWithNewData);

        const trackedEntityAttributeValue = service.getTrackedEntityAttributeValue(formGroup) as any;

        expect(trackedEntityAttributeValue).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityAttributeValue for empty TrackedEntityAttributeValue initial value', () => {
        const formGroup = service.createTrackedEntityAttributeValueFormGroup();

        const trackedEntityAttributeValue = service.getTrackedEntityAttributeValue(formGroup) as any;

        expect(trackedEntityAttributeValue).toMatchObject({});
      });

      it('should return ITrackedEntityAttributeValue', () => {
        const formGroup = service.createTrackedEntityAttributeValueFormGroup(sampleWithRequiredData);

        const trackedEntityAttributeValue = service.getTrackedEntityAttributeValue(formGroup) as any;

        expect(trackedEntityAttributeValue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityAttributeValue should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityAttributeValueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityAttributeValue should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityAttributeValueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
