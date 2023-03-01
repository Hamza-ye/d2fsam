import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-stage-data-element.test-samples';

import { ProgramStageDataElementFormService } from './program-stage-data-element-form.service';

describe('ProgramStageDataElement Form Service', () => {
  let service: ProgramStageDataElementFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramStageDataElementFormService);
  });

  describe('Service methods', () => {
    describe('createProgramStageDataElementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramStageDataElementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            compulsory: expect.any(Object),
            allowProvidedElsewhere: expect.any(Object),
            sortOrder: expect.any(Object),
            displayInReports: expect.any(Object),
            allowFutureDate: expect.any(Object),
            skipSynchronization: expect.any(Object),
            defaultValue: expect.any(Object),
            programStage: expect.any(Object),
            dataElement: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IProgramStageDataElement should create a new form with FormGroup', () => {
        const formGroup = service.createProgramStageDataElementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            compulsory: expect.any(Object),
            allowProvidedElsewhere: expect.any(Object),
            sortOrder: expect.any(Object),
            displayInReports: expect.any(Object),
            allowFutureDate: expect.any(Object),
            skipSynchronization: expect.any(Object),
            defaultValue: expect.any(Object),
            programStage: expect.any(Object),
            dataElement: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramStageDataElement', () => {
      it('should return NewProgramStageDataElement for default ProgramStageDataElement initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramStageDataElementFormGroup(sampleWithNewData);

        const programStageDataElement = service.getProgramStageDataElement(formGroup) as any;

        expect(programStageDataElement).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramStageDataElement for empty ProgramStageDataElement initial value', () => {
        const formGroup = service.createProgramStageDataElementFormGroup();

        const programStageDataElement = service.getProgramStageDataElement(formGroup) as any;

        expect(programStageDataElement).toMatchObject({});
      });

      it('should return IProgramStageDataElement', () => {
        const formGroup = service.createProgramStageDataElementFormGroup(sampleWithRequiredData);

        const programStageDataElement = service.getProgramStageDataElement(formGroup) as any;

        expect(programStageDataElement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramStageDataElement should not enable id FormControl', () => {
        const formGroup = service.createProgramStageDataElementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramStageDataElement should disable id FormControl', () => {
        const formGroup = service.createProgramStageDataElementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
