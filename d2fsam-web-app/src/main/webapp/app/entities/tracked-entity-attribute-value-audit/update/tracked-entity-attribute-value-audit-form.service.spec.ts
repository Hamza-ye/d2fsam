import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-attribute-value-audit.test-samples';

import { TrackedEntityAttributeValueAuditFormService } from './tracked-entity-attribute-value-audit-form.service';

describe('TrackedEntityAttributeValueAudit Form Service', () => {
  let service: TrackedEntityAttributeValueAuditFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityAttributeValueAuditFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityAttributeValueAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            encryptedValue: expect.any(Object),
            plainValue: expect.any(Object),
            value: expect.any(Object),
            modifiedBy: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            auditType: expect.any(Object),
            attribute: expect.any(Object),
            entityInstance: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityAttributeValueAudit should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            encryptedValue: expect.any(Object),
            plainValue: expect.any(Object),
            value: expect.any(Object),
            modifiedBy: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            auditType: expect.any(Object),
            attribute: expect.any(Object),
            entityInstance: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityAttributeValueAudit', () => {
      it('should return NewTrackedEntityAttributeValueAudit for default TrackedEntityAttributeValueAudit initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup(sampleWithNewData);

        const trackedEntityAttributeValueAudit = service.getTrackedEntityAttributeValueAudit(formGroup) as any;

        expect(trackedEntityAttributeValueAudit).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityAttributeValueAudit for empty TrackedEntityAttributeValueAudit initial value', () => {
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup();

        const trackedEntityAttributeValueAudit = service.getTrackedEntityAttributeValueAudit(formGroup) as any;

        expect(trackedEntityAttributeValueAudit).toMatchObject({});
      });

      it('should return ITrackedEntityAttributeValueAudit', () => {
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup(sampleWithRequiredData);

        const trackedEntityAttributeValueAudit = service.getTrackedEntityAttributeValueAudit(formGroup) as any;

        expect(trackedEntityAttributeValueAudit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityAttributeValueAudit should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityAttributeValueAudit should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityAttributeValueAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
