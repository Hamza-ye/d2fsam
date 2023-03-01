import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-data-value-audit.test-samples';

import { TrackedEntityDataValueAuditFormService } from './tracked-entity-data-value-audit-form.service';

describe('TrackedEntityDataValueAudit Form Service', () => {
  let service: TrackedEntityDataValueAuditFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityDataValueAuditFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityDataValueAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            modifiedBy: expect.any(Object),
            providedElsewhere: expect.any(Object),
            auditType: expect.any(Object),
            programStageInstance: expect.any(Object),
            dataElement: expect.any(Object),
            period: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityDataValueAudit should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            modifiedBy: expect.any(Object),
            providedElsewhere: expect.any(Object),
            auditType: expect.any(Object),
            programStageInstance: expect.any(Object),
            dataElement: expect.any(Object),
            period: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityDataValueAudit', () => {
      it('should return NewTrackedEntityDataValueAudit for default TrackedEntityDataValueAudit initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup(sampleWithNewData);

        const trackedEntityDataValueAudit = service.getTrackedEntityDataValueAudit(formGroup) as any;

        expect(trackedEntityDataValueAudit).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityDataValueAudit for empty TrackedEntityDataValueAudit initial value', () => {
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup();

        const trackedEntityDataValueAudit = service.getTrackedEntityDataValueAudit(formGroup) as any;

        expect(trackedEntityDataValueAudit).toMatchObject({});
      });

      it('should return ITrackedEntityDataValueAudit', () => {
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup(sampleWithRequiredData);

        const trackedEntityDataValueAudit = service.getTrackedEntityDataValueAudit(formGroup) as any;

        expect(trackedEntityDataValueAudit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityDataValueAudit should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityDataValueAudit should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityDataValueAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
