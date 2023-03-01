import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-temp-ownership-audit.test-samples';

import { ProgramTempOwnershipAuditFormService } from './program-temp-ownership-audit-form.service';

describe('ProgramTempOwnershipAudit Form Service', () => {
  let service: ProgramTempOwnershipAuditFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramTempOwnershipAuditFormService);
  });

  describe('Service methods', () => {
    describe('createProgramTempOwnershipAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramTempOwnershipAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            created: expect.any(Object),
            accessedBy: expect.any(Object),
            program: expect.any(Object),
            entityInstance: expect.any(Object),
          })
        );
      });

      it('passing IProgramTempOwnershipAudit should create a new form with FormGroup', () => {
        const formGroup = service.createProgramTempOwnershipAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            created: expect.any(Object),
            accessedBy: expect.any(Object),
            program: expect.any(Object),
            entityInstance: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramTempOwnershipAudit', () => {
      it('should return NewProgramTempOwnershipAudit for default ProgramTempOwnershipAudit initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramTempOwnershipAuditFormGroup(sampleWithNewData);

        const programTempOwnershipAudit = service.getProgramTempOwnershipAudit(formGroup) as any;

        expect(programTempOwnershipAudit).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramTempOwnershipAudit for empty ProgramTempOwnershipAudit initial value', () => {
        const formGroup = service.createProgramTempOwnershipAuditFormGroup();

        const programTempOwnershipAudit = service.getProgramTempOwnershipAudit(formGroup) as any;

        expect(programTempOwnershipAudit).toMatchObject({});
      });

      it('should return IProgramTempOwnershipAudit', () => {
        const formGroup = service.createProgramTempOwnershipAuditFormGroup(sampleWithRequiredData);

        const programTempOwnershipAudit = service.getProgramTempOwnershipAudit(formGroup) as any;

        expect(programTempOwnershipAudit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramTempOwnershipAudit should not enable id FormControl', () => {
        const formGroup = service.createProgramTempOwnershipAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramTempOwnershipAudit should disable id FormControl', () => {
        const formGroup = service.createProgramTempOwnershipAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
