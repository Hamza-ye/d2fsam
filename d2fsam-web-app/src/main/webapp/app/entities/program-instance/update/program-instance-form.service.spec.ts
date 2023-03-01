import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-instance.test-samples';

import { ProgramInstanceFormService } from './program-instance-form.service';

describe('ProgramInstance Form Service', () => {
  let service: ProgramInstanceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramInstanceFormService);
  });

  describe('Service methods', () => {
    describe('createProgramInstanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramInstanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            uuid: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdAtClient: expect.any(Object),
            updatedAtClient: expect.any(Object),
            lastSynchronized: expect.any(Object),
            incidentDate: expect.any(Object),
            enrollmentDate: expect.any(Object),
            periodLabel: expect.any(Object),
            endDate: expect.any(Object),
            status: expect.any(Object),
            storedBy: expect.any(Object),
            completedBy: expect.any(Object),
            completedDate: expect.any(Object),
            followup: expect.any(Object),
            deleted: expect.any(Object),
            deletedAt: expect.any(Object),
            entityInstance: expect.any(Object),
            program: expect.any(Object),
            organisationUnit: expect.any(Object),
            activity: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            approvedBy: expect.any(Object),
            comments: expect.any(Object),
          })
        );
      });

      it('passing IProgramInstance should create a new form with FormGroup', () => {
        const formGroup = service.createProgramInstanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            uuid: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdAtClient: expect.any(Object),
            updatedAtClient: expect.any(Object),
            lastSynchronized: expect.any(Object),
            incidentDate: expect.any(Object),
            enrollmentDate: expect.any(Object),
            periodLabel: expect.any(Object),
            endDate: expect.any(Object),
            status: expect.any(Object),
            storedBy: expect.any(Object),
            completedBy: expect.any(Object),
            completedDate: expect.any(Object),
            followup: expect.any(Object),
            deleted: expect.any(Object),
            deletedAt: expect.any(Object),
            entityInstance: expect.any(Object),
            program: expect.any(Object),
            organisationUnit: expect.any(Object),
            activity: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            approvedBy: expect.any(Object),
            comments: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramInstance', () => {
      it('should return NewProgramInstance for default ProgramInstance initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramInstanceFormGroup(sampleWithNewData);

        const programInstance = service.getProgramInstance(formGroup) as any;

        expect(programInstance).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramInstance for empty ProgramInstance initial value', () => {
        const formGroup = service.createProgramInstanceFormGroup();

        const programInstance = service.getProgramInstance(formGroup) as any;

        expect(programInstance).toMatchObject({});
      });

      it('should return IProgramInstance', () => {
        const formGroup = service.createProgramInstanceFormGroup(sampleWithRequiredData);

        const programInstance = service.getProgramInstance(formGroup) as any;

        expect(programInstance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramInstance should not enable id FormControl', () => {
        const formGroup = service.createProgramInstanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramInstance should disable id FormControl', () => {
        const formGroup = service.createProgramInstanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
