import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-stage-instance.test-samples';

import { ProgramStageInstanceFormService } from './program-stage-instance-form.service';

describe('ProgramStageInstance Form Service', () => {
  let service: ProgramStageInstanceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramStageInstanceFormService);
  });

  describe('Service methods', () => {
    describe('createProgramStageInstanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramStageInstanceFormGroup();

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
            dueDate: expect.any(Object),
            executionDate: expect.any(Object),
            status: expect.any(Object),
            storedBy: expect.any(Object),
            completedBy: expect.any(Object),
            completedDate: expect.any(Object),
            deleted: expect.any(Object),
            deletedAt: expect.any(Object),
            programInstance: expect.any(Object),
            programStage: expect.any(Object),
            organisationUnit: expect.any(Object),
            assignedUser: expect.any(Object),
            period: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            approvedBy: expect.any(Object),
            comments: expect.any(Object),
          })
        );
      });

      it('passing IProgramStageInstance should create a new form with FormGroup', () => {
        const formGroup = service.createProgramStageInstanceFormGroup(sampleWithRequiredData);

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
            dueDate: expect.any(Object),
            executionDate: expect.any(Object),
            status: expect.any(Object),
            storedBy: expect.any(Object),
            completedBy: expect.any(Object),
            completedDate: expect.any(Object),
            deleted: expect.any(Object),
            deletedAt: expect.any(Object),
            programInstance: expect.any(Object),
            programStage: expect.any(Object),
            organisationUnit: expect.any(Object),
            assignedUser: expect.any(Object),
            period: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            approvedBy: expect.any(Object),
            comments: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramStageInstance', () => {
      it('should return NewProgramStageInstance for default ProgramStageInstance initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramStageInstanceFormGroup(sampleWithNewData);

        const programStageInstance = service.getProgramStageInstance(formGroup) as any;

        expect(programStageInstance).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramStageInstance for empty ProgramStageInstance initial value', () => {
        const formGroup = service.createProgramStageInstanceFormGroup();

        const programStageInstance = service.getProgramStageInstance(formGroup) as any;

        expect(programStageInstance).toMatchObject({});
      });

      it('should return IProgramStageInstance', () => {
        const formGroup = service.createProgramStageInstanceFormGroup(sampleWithRequiredData);

        const programStageInstance = service.getProgramStageInstance(formGroup) as any;

        expect(programStageInstance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramStageInstance should not enable id FormControl', () => {
        const formGroup = service.createProgramStageInstanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramStageInstance should disable id FormControl', () => {
        const formGroup = service.createProgramStageInstanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
