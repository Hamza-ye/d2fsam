import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-stage-instance-filter.test-samples';

import { ProgramStageInstanceFilterFormService } from './program-stage-instance-filter-form.service';

describe('ProgramStageInstanceFilter Form Service', () => {
  let service: ProgramStageInstanceFilterFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramStageInstanceFilterFormService);
  });

  describe('Service methods', () => {
    describe('createProgramStageInstanceFilterFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramStageInstanceFilterFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            program: expect.any(Object),
            programStage: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IProgramStageInstanceFilter should create a new form with FormGroup', () => {
        const formGroup = service.createProgramStageInstanceFilterFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            program: expect.any(Object),
            programStage: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramStageInstanceFilter', () => {
      it('should return NewProgramStageInstanceFilter for default ProgramStageInstanceFilter initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramStageInstanceFilterFormGroup(sampleWithNewData);

        const programStageInstanceFilter = service.getProgramStageInstanceFilter(formGroup) as any;

        expect(programStageInstanceFilter).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramStageInstanceFilter for empty ProgramStageInstanceFilter initial value', () => {
        const formGroup = service.createProgramStageInstanceFilterFormGroup();

        const programStageInstanceFilter = service.getProgramStageInstanceFilter(formGroup) as any;

        expect(programStageInstanceFilter).toMatchObject({});
      });

      it('should return IProgramStageInstanceFilter', () => {
        const formGroup = service.createProgramStageInstanceFilterFormGroup(sampleWithRequiredData);

        const programStageInstanceFilter = service.getProgramStageInstanceFilter(formGroup) as any;

        expect(programStageInstanceFilter).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramStageInstanceFilter should not enable id FormControl', () => {
        const formGroup = service.createProgramStageInstanceFilterFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramStageInstanceFilter should disable id FormControl', () => {
        const formGroup = service.createProgramStageInstanceFilterFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
