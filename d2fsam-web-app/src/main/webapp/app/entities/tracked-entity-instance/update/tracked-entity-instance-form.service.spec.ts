import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-instance.test-samples';

import { TrackedEntityInstanceFormService } from './tracked-entity-instance-form.service';

describe('TrackedEntityInstance Form Service', () => {
  let service: TrackedEntityInstanceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityInstanceFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityInstanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            uuid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdAtClient: expect.any(Object),
            updatedAtClient: expect.any(Object),
            lastSynchronized: expect.any(Object),
            featureType: expect.any(Object),
            coordinates: expect.any(Object),
            potentialDuplicate: expect.any(Object),
            deleted: expect.any(Object),
            storedBy: expect.any(Object),
            inactive: expect.any(Object),
            period: expect.any(Object),
            organisationUnit: expect.any(Object),
            trackedEntityType: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityInstance should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityInstanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            uuid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdAtClient: expect.any(Object),
            updatedAtClient: expect.any(Object),
            lastSynchronized: expect.any(Object),
            featureType: expect.any(Object),
            coordinates: expect.any(Object),
            potentialDuplicate: expect.any(Object),
            deleted: expect.any(Object),
            storedBy: expect.any(Object),
            inactive: expect.any(Object),
            period: expect.any(Object),
            organisationUnit: expect.any(Object),
            trackedEntityType: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityInstance', () => {
      it('should return NewTrackedEntityInstance for default TrackedEntityInstance initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityInstanceFormGroup(sampleWithNewData);

        const trackedEntityInstance = service.getTrackedEntityInstance(formGroup) as any;

        expect(trackedEntityInstance).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityInstance for empty TrackedEntityInstance initial value', () => {
        const formGroup = service.createTrackedEntityInstanceFormGroup();

        const trackedEntityInstance = service.getTrackedEntityInstance(formGroup) as any;

        expect(trackedEntityInstance).toMatchObject({});
      });

      it('should return ITrackedEntityInstance', () => {
        const formGroup = service.createTrackedEntityInstanceFormGroup(sampleWithRequiredData);

        const trackedEntityInstance = service.getTrackedEntityInstance(formGroup) as any;

        expect(trackedEntityInstance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityInstance should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityInstance should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
