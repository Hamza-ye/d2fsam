import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-stage.test-samples';

import { ProgramStageFormService } from './program-stage-form.service';

describe('ProgramStage Form Service', () => {
  let service: ProgramStageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramStageFormService);
  });

  describe('Service methods', () => {
    describe('createProgramStageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramStageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            minDaysFromStart: expect.any(Object),
            repeatable: expect.any(Object),
            executionDateLabel: expect.any(Object),
            dueDateLabel: expect.any(Object),
            autoGenerateEvent: expect.any(Object),
            validationStrategy: expect.any(Object),
            blockEntryForm: expect.any(Object),
            openAfterEnrollment: expect.any(Object),
            generatedByEnrollmentDate: expect.any(Object),
            sortOrder: expect.any(Object),
            hideDueDate: expect.any(Object),
            featureType: expect.any(Object),
            enableUserAssignment: expect.any(Object),
            enableTeamAssignment: expect.any(Object),
            inactive: expect.any(Object),
            periodType: expect.any(Object),
            program: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IProgramStage should create a new form with FormGroup', () => {
        const formGroup = service.createProgramStageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            minDaysFromStart: expect.any(Object),
            repeatable: expect.any(Object),
            executionDateLabel: expect.any(Object),
            dueDateLabel: expect.any(Object),
            autoGenerateEvent: expect.any(Object),
            validationStrategy: expect.any(Object),
            blockEntryForm: expect.any(Object),
            openAfterEnrollment: expect.any(Object),
            generatedByEnrollmentDate: expect.any(Object),
            sortOrder: expect.any(Object),
            hideDueDate: expect.any(Object),
            featureType: expect.any(Object),
            enableUserAssignment: expect.any(Object),
            enableTeamAssignment: expect.any(Object),
            inactive: expect.any(Object),
            periodType: expect.any(Object),
            program: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramStage', () => {
      it('should return NewProgramStage for default ProgramStage initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramStageFormGroup(sampleWithNewData);

        const programStage = service.getProgramStage(formGroup) as any;

        expect(programStage).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramStage for empty ProgramStage initial value', () => {
        const formGroup = service.createProgramStageFormGroup();

        const programStage = service.getProgramStage(formGroup) as any;

        expect(programStage).toMatchObject({});
      });

      it('should return IProgramStage', () => {
        const formGroup = service.createProgramStageFormGroup(sampleWithRequiredData);

        const programStage = service.getProgramStage(formGroup) as any;

        expect(programStage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramStage should not enable id FormControl', () => {
        const formGroup = service.createProgramStageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramStage should disable id FormControl', () => {
        const formGroup = service.createProgramStageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
