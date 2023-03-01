import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-ownership-history.test-samples';

import { ProgramOwnershipHistoryFormService } from './program-ownership-history-form.service';

describe('ProgramOwnershipHistory Form Service', () => {
  let service: ProgramOwnershipHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramOwnershipHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createProgramOwnershipHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramOwnershipHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            createdBy: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            program: expect.any(Object),
            entityInstance: expect.any(Object),
            organisationUnit: expect.any(Object),
          })
        );
      });

      it('passing IProgramOwnershipHistory should create a new form with FormGroup', () => {
        const formGroup = service.createProgramOwnershipHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            createdBy: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            program: expect.any(Object),
            entityInstance: expect.any(Object),
            organisationUnit: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramOwnershipHistory', () => {
      it('should return NewProgramOwnershipHistory for default ProgramOwnershipHistory initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramOwnershipHistoryFormGroup(sampleWithNewData);

        const programOwnershipHistory = service.getProgramOwnershipHistory(formGroup) as any;

        expect(programOwnershipHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramOwnershipHistory for empty ProgramOwnershipHistory initial value', () => {
        const formGroup = service.createProgramOwnershipHistoryFormGroup();

        const programOwnershipHistory = service.getProgramOwnershipHistory(formGroup) as any;

        expect(programOwnershipHistory).toMatchObject({});
      });

      it('should return IProgramOwnershipHistory', () => {
        const formGroup = service.createProgramOwnershipHistoryFormGroup(sampleWithRequiredData);

        const programOwnershipHistory = service.getProgramOwnershipHistory(formGroup) as any;

        expect(programOwnershipHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramOwnershipHistory should not enable id FormControl', () => {
        const formGroup = service.createProgramOwnershipHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramOwnershipHistory should disable id FormControl', () => {
        const formGroup = service.createProgramOwnershipHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
