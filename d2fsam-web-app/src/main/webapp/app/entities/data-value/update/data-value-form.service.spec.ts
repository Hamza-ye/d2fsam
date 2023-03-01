import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../data-value.test-samples';

import { DataValueFormService } from './data-value-form.service';

describe('DataValue Form Service', () => {
  let service: DataValueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataValueFormService);
  });

  describe('Service methods', () => {
    describe('createDataValueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDataValueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value: expect.any(Object),
            storedBy: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            comment: expect.any(Object),
            followup: expect.any(Object),
            deleted: expect.any(Object),
            auditType: expect.any(Object),
            dataElement: expect.any(Object),
            period: expect.any(Object),
            source: expect.any(Object),
          })
        );
      });

      it('passing IDataValue should create a new form with FormGroup', () => {
        const formGroup = service.createDataValueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            value: expect.any(Object),
            storedBy: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            comment: expect.any(Object),
            followup: expect.any(Object),
            deleted: expect.any(Object),
            auditType: expect.any(Object),
            dataElement: expect.any(Object),
            period: expect.any(Object),
            source: expect.any(Object),
          })
        );
      });
    });

    describe('getDataValue', () => {
      it('should return NewDataValue for default DataValue initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDataValueFormGroup(sampleWithNewData);

        const dataValue = service.getDataValue(formGroup) as any;

        expect(dataValue).toMatchObject(sampleWithNewData);
      });

      it('should return NewDataValue for empty DataValue initial value', () => {
        const formGroup = service.createDataValueFormGroup();

        const dataValue = service.getDataValue(formGroup) as any;

        expect(dataValue).toMatchObject({});
      });

      it('should return IDataValue', () => {
        const formGroup = service.createDataValueFormGroup(sampleWithRequiredData);

        const dataValue = service.getDataValue(formGroup) as any;

        expect(dataValue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDataValue should not enable id FormControl', () => {
        const formGroup = service.createDataValueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDataValue should disable id FormControl', () => {
        const formGroup = service.createDataValueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
