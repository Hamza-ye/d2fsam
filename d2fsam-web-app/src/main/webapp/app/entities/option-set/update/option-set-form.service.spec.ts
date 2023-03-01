import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../option-set.test-samples';

import { OptionSetFormService } from './option-set-form.service';

describe('OptionSet Form Service', () => {
  let service: OptionSetFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OptionSetFormService);
  });

  describe('Service methods', () => {
    describe('createOptionSetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOptionSetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            valueType: expect.any(Object),
            version: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IOptionSet should create a new form with FormGroup', () => {
        const formGroup = service.createOptionSetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            valueType: expect.any(Object),
            version: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getOptionSet', () => {
      it('should return NewOptionSet for default OptionSet initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOptionSetFormGroup(sampleWithNewData);

        const optionSet = service.getOptionSet(formGroup) as any;

        expect(optionSet).toMatchObject(sampleWithNewData);
      });

      it('should return NewOptionSet for empty OptionSet initial value', () => {
        const formGroup = service.createOptionSetFormGroup();

        const optionSet = service.getOptionSet(formGroup) as any;

        expect(optionSet).toMatchObject({});
      });

      it('should return IOptionSet', () => {
        const formGroup = service.createOptionSetFormGroup(sampleWithRequiredData);

        const optionSet = service.getOptionSet(formGroup) as any;

        expect(optionSet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOptionSet should not enable id FormControl', () => {
        const formGroup = service.createOptionSetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOptionSet should disable id FormControl', () => {
        const formGroup = service.createOptionSetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
