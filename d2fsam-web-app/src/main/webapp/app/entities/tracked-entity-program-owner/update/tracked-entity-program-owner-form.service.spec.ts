import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-program-owner.test-samples';

import { TrackedEntityProgramOwnerFormService } from './tracked-entity-program-owner-form.service';

describe('TrackedEntityProgramOwner Form Service', () => {
  let service: TrackedEntityProgramOwnerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityProgramOwnerFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityProgramOwnerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdBy: expect.any(Object),
            entityInstance: expect.any(Object),
            program: expect.any(Object),
            organisationUnit: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityProgramOwner should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdBy: expect.any(Object),
            entityInstance: expect.any(Object),
            program: expect.any(Object),
            organisationUnit: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityProgramOwner', () => {
      it('should return NewTrackedEntityProgramOwner for default TrackedEntityProgramOwner initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup(sampleWithNewData);

        const trackedEntityProgramOwner = service.getTrackedEntityProgramOwner(formGroup) as any;

        expect(trackedEntityProgramOwner).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityProgramOwner for empty TrackedEntityProgramOwner initial value', () => {
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup();

        const trackedEntityProgramOwner = service.getTrackedEntityProgramOwner(formGroup) as any;

        expect(trackedEntityProgramOwner).toMatchObject({});
      });

      it('should return ITrackedEntityProgramOwner', () => {
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup(sampleWithRequiredData);

        const trackedEntityProgramOwner = service.getTrackedEntityProgramOwner(formGroup) as any;

        expect(trackedEntityProgramOwner).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityProgramOwner should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityProgramOwner should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityProgramOwnerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
