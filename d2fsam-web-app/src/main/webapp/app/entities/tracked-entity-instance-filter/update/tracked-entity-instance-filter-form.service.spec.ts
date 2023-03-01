import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-instance-filter.test-samples';

import { TrackedEntityInstanceFilterFormService } from './tracked-entity-instance-filter-form.service';

describe('TrackedEntityInstanceFilter Form Service', () => {
  let service: TrackedEntityInstanceFilterFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityInstanceFilterFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityInstanceFilterFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            sortOrder: expect.any(Object),
            enrollmentStatus: expect.any(Object),
            program: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityInstanceFilter should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            sortOrder: expect.any(Object),
            enrollmentStatus: expect.any(Object),
            program: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityInstanceFilter', () => {
      it('should return NewTrackedEntityInstanceFilter for default TrackedEntityInstanceFilter initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup(sampleWithNewData);

        const trackedEntityInstanceFilter = service.getTrackedEntityInstanceFilter(formGroup) as any;

        expect(trackedEntityInstanceFilter).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityInstanceFilter for empty TrackedEntityInstanceFilter initial value', () => {
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup();

        const trackedEntityInstanceFilter = service.getTrackedEntityInstanceFilter(formGroup) as any;

        expect(trackedEntityInstanceFilter).toMatchObject({});
      });

      it('should return ITrackedEntityInstanceFilter', () => {
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup(sampleWithRequiredData);

        const trackedEntityInstanceFilter = service.getTrackedEntityInstanceFilter(formGroup) as any;

        expect(trackedEntityInstanceFilter).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityInstanceFilter should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityInstanceFilter should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceFilterFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
