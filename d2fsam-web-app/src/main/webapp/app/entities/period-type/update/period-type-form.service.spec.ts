import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../period-type.test-samples';

import { PeriodTypeFormService } from './period-type-form.service';

describe('PeriodType Form Service', () => {
  let service: PeriodTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PeriodTypeFormService);
  });

  describe('Service methods', () => {
    describe('createPeriodTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPeriodTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });

      it('passing IPeriodType should create a new form with FormGroup', () => {
        const formGroup = service.createPeriodTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });
    });

    describe('getPeriodType', () => {
      it('should return NewPeriodType for default PeriodType initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPeriodTypeFormGroup(sampleWithNewData);

        const periodType = service.getPeriodType(formGroup) as any;

        expect(periodType).toMatchObject(sampleWithNewData);
      });

      it('should return NewPeriodType for empty PeriodType initial value', () => {
        const formGroup = service.createPeriodTypeFormGroup();

        const periodType = service.getPeriodType(formGroup) as any;

        expect(periodType).toMatchObject({});
      });

      it('should return IPeriodType', () => {
        const formGroup = service.createPeriodTypeFormGroup(sampleWithRequiredData);

        const periodType = service.getPeriodType(formGroup) as any;

        expect(periodType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPeriodType should not enable id FormControl', () => {
        const formGroup = service.createPeriodTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPeriodType should disable id FormControl', () => {
        const formGroup = service.createPeriodTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
