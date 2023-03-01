import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../program-temp-owner.test-samples';

import { ProgramTempOwnerFormService } from './program-temp-owner-form.service';

describe('ProgramTempOwner Form Service', () => {
  let service: ProgramTempOwnerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramTempOwnerFormService);
  });

  describe('Service methods', () => {
    describe('createProgramTempOwnerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgramTempOwnerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            validTill: expect.any(Object),
            program: expect.any(Object),
            entityInstance: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IProgramTempOwner should create a new form with FormGroup', () => {
        const formGroup = service.createProgramTempOwnerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            validTill: expect.any(Object),
            program: expect.any(Object),
            entityInstance: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getProgramTempOwner', () => {
      it('should return NewProgramTempOwner for default ProgramTempOwner initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProgramTempOwnerFormGroup(sampleWithNewData);

        const programTempOwner = service.getProgramTempOwner(formGroup) as any;

        expect(programTempOwner).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgramTempOwner for empty ProgramTempOwner initial value', () => {
        const formGroup = service.createProgramTempOwnerFormGroup();

        const programTempOwner = service.getProgramTempOwner(formGroup) as any;

        expect(programTempOwner).toMatchObject({});
      });

      it('should return IProgramTempOwner', () => {
        const formGroup = service.createProgramTempOwnerFormGroup(sampleWithRequiredData);

        const programTempOwner = service.getProgramTempOwner(formGroup) as any;

        expect(programTempOwner).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgramTempOwner should not enable id FormControl', () => {
        const formGroup = service.createProgramTempOwnerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgramTempOwner should disable id FormControl', () => {
        const formGroup = service.createProgramTempOwnerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
