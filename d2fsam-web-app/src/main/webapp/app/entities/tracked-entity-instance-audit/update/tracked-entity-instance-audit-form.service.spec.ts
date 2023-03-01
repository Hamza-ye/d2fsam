import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-instance-audit.test-samples';

import { TrackedEntityInstanceAuditFormService } from './tracked-entity-instance-audit-form.service';

describe('TrackedEntityInstanceAudit Form Service', () => {
  let service: TrackedEntityInstanceAuditFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityInstanceAuditFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityInstanceAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            trackedEntityInstance: expect.any(Object),
            comment: expect.any(Object),
            created: expect.any(Object),
            accessedBy: expect.any(Object),
            auditType: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityInstanceAudit should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            trackedEntityInstance: expect.any(Object),
            comment: expect.any(Object),
            created: expect.any(Object),
            accessedBy: expect.any(Object),
            auditType: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityInstanceAudit', () => {
      it('should return NewTrackedEntityInstanceAudit for default TrackedEntityInstanceAudit initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup(sampleWithNewData);

        const trackedEntityInstanceAudit = service.getTrackedEntityInstanceAudit(formGroup) as any;

        expect(trackedEntityInstanceAudit).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityInstanceAudit for empty TrackedEntityInstanceAudit initial value', () => {
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup();

        const trackedEntityInstanceAudit = service.getTrackedEntityInstanceAudit(formGroup) as any;

        expect(trackedEntityInstanceAudit).toMatchObject({});
      });

      it('should return ITrackedEntityInstanceAudit', () => {
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup(sampleWithRequiredData);

        const trackedEntityInstanceAudit = service.getTrackedEntityInstanceAudit(formGroup) as any;

        expect(trackedEntityInstanceAudit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityInstanceAudit should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityInstanceAudit should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityInstanceAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
