import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-tracked-entity-attribute.test-samples';

import { ProgramTrackedEntityAttributeFormService } from './program-tracked-entity-attribute-form.service';

describe('ProgramTrackedEntityAttribute Form Service', () => {
  let service: ProgramTrackedEntityAttributeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramTrackedEntityAttributeFormService);
  });

  describe('Service methods', () => {
    describe('createProgramTrackedEntityAttributeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            displayInList: expect.any(Object),
            sortOrder: expect.any(Object),
            mandatory: expect.any(Object),
            allowFutureDate: expect.any(Object),
            searchable: expect.any(Object),
            defaultValue: expect.any(Object),
            program: expect.any(Object),
            attribute: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IProgramTrackedEntityAttribute should create a new form with FormGroup', () => {
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            displayInList: expect.any(Object),
            sortOrder: expect.any(Object),
            mandatory: expect.any(Object),
            allowFutureDate: expect.any(Object),
            searchable: expect.any(Object),
            defaultValue: expect.any(Object),
            program: expect.any(Object),
            attribute: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramTrackedEntityAttribute', () => {
      it('should return NewProgramTrackedEntityAttribute for default ProgramTrackedEntityAttribute initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup(sampleWithNewData);

        const programTrackedEntityAttribute = service.getProgramTrackedEntityAttribute(formGroup) as any;

        expect(programTrackedEntityAttribute).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramTrackedEntityAttribute for empty ProgramTrackedEntityAttribute initial value', () => {
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup();

        const programTrackedEntityAttribute = service.getProgramTrackedEntityAttribute(formGroup) as any;

        expect(programTrackedEntityAttribute).toMatchObject({});
      });

      it('should return IProgramTrackedEntityAttribute', () => {
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup(sampleWithRequiredData);

        const programTrackedEntityAttribute = service.getProgramTrackedEntityAttribute(formGroup) as any;

        expect(programTrackedEntityAttribute).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramTrackedEntityAttribute should not enable id FormControl', () => {
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramTrackedEntityAttribute should disable id FormControl', () => {
        const formGroup = service.createProgramTrackedEntityAttributeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
