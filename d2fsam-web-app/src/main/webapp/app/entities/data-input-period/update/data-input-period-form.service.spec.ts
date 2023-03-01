import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../data-input-period.test-samples';

import { DataInputPeriodFormService } from './data-input-period-form.service';

describe('DataInputPeriod Form Service', () => {
  let service: DataInputPeriodFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataInputPeriodFormService);
  });

  describe('Service methods', () => {
    describe('createDataInputPeriodFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDataInputPeriodFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            openingDate: expect.any(Object),
            closingDate: expect.any(Object),
            period: expect.any(Object),
          })
        );
      });

      it('passing IDataInputPeriod should create a new form with FormGroup', () => {
        const formGroup = service.createDataInputPeriodFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            openingDate: expect.any(Object),
            closingDate: expect.any(Object),
            period: expect.any(Object),
          })
        );
      });
    });

    describe('getDataInputPeriod', () => {
      it('should return NewDataInputPeriod for default DataInputPeriod initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDataInputPeriodFormGroup(sampleWithNewData);

        const dataInputPeriod = service.getDataInputPeriod(formGroup) as any;

        expect(dataInputPeriod).toMatchObject(sampleWithNewData);
      });

      it('should return NewDataInputPeriod for empty DataInputPeriod initial value', () => {
        const formGroup = service.createDataInputPeriodFormGroup();

        const dataInputPeriod = service.getDataInputPeriod(formGroup) as any;

        expect(dataInputPeriod).toMatchObject({});
      });

      it('should return IDataInputPeriod', () => {
        const formGroup = service.createDataInputPeriodFormGroup(sampleWithRequiredData);

        const dataInputPeriod = service.getDataInputPeriod(formGroup) as any;

        expect(dataInputPeriod).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDataInputPeriod should not enable id FormControl', () => {
        const formGroup = service.createDataInputPeriodFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDataInputPeriod should disable id FormControl', () => {
        const formGroup = service.createDataInputPeriodFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
