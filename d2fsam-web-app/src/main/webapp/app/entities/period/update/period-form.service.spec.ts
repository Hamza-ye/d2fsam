import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../period.test-samples';

import { PeriodFormService } from './period-form.service';

describe('Period Form Service', () => {
  let service: PeriodFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PeriodFormService);
  });

  describe('Service methods', () => {
    describe('createPeriodFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPeriodFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            periodType: expect.any(Object),
          })
        );
      });

      it('passing IPeriod should create a new form with FormGroup', () => {
        const formGroup = service.createPeriodFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            periodType: expect.any(Object),
          })
        );
      });
    });

    describe('getPeriod', () => {
      it('should return NewPeriod for default Period initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPeriodFormGroup(sampleWithNewData);

        const period = service.getPeriod(formGroup) as any;

        expect(period).toMatchObject(sampleWithNewData);
      });

      it('should return NewPeriod for empty Period initial value', () => {
        const formGroup = service.createPeriodFormGroup();

        const period = service.getPeriod(formGroup) as any;

        expect(period).toMatchObject({});
      });

      it('should return IPeriod', () => {
        const formGroup = service.createPeriodFormGroup(sampleWithRequiredData);

        const period = service.getPeriod(formGroup) as any;

        expect(period).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPeriod should not enable id FormControl', () => {
        const formGroup = service.createPeriodFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPeriod should disable id FormControl', () => {
        const formGroup = service.createPeriodFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
