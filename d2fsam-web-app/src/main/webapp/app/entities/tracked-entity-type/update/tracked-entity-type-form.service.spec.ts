import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-type.test-samples';

import { TrackedEntityTypeFormService } from './tracked-entity-type-form.service';

describe('TrackedEntityType Form Service', () => {
  let service: TrackedEntityTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityTypeFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            maxTeiCountToReturn: expect.any(Object),
            allowAuditLog: expect.any(Object),
            featureType: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityType should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            maxTeiCountToReturn: expect.any(Object),
            allowAuditLog: expect.any(Object),
            featureType: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityType', () => {
      it('should return NewTrackedEntityType for default TrackedEntityType initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityTypeFormGroup(sampleWithNewData);

        const trackedEntityType = service.getTrackedEntityType(formGroup) as any;

        expect(trackedEntityType).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityType for empty TrackedEntityType initial value', () => {
        const formGroup = service.createTrackedEntityTypeFormGroup();

        const trackedEntityType = service.getTrackedEntityType(formGroup) as any;

        expect(trackedEntityType).toMatchObject({});
      });

      it('should return ITrackedEntityType', () => {
        const formGroup = service.createTrackedEntityTypeFormGroup(sampleWithRequiredData);

        const trackedEntityType = service.getTrackedEntityType(formGroup) as any;

        expect(trackedEntityType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityType should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityType should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
