import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../data-element.test-samples';

import { DataElementFormService } from './data-element-form.service';

describe('DataElement Form Service', () => {
  let service: DataElementFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataElementFormService);
  });

  describe('Service methods', () => {
    describe('createDataElementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDataElementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            valueType: expect.any(Object),
            aggregationType: expect.any(Object),
            displayInListNoProgram: expect.any(Object),
            zeroIsSignificant: expect.any(Object),
            mandatory: expect.any(Object),
            uniqueAttribute: expect.any(Object),
            fieldMask: expect.any(Object),
            orgunitScope: expect.any(Object),
            skipSynchronization: expect.any(Object),
            confidential: expect.any(Object),
            optionSet: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IDataElement should create a new form with FormGroup', () => {
        const formGroup = service.createDataElementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            valueType: expect.any(Object),
            aggregationType: expect.any(Object),
            displayInListNoProgram: expect.any(Object),
            zeroIsSignificant: expect.any(Object),
            mandatory: expect.any(Object),
            uniqueAttribute: expect.any(Object),
            fieldMask: expect.any(Object),
            orgunitScope: expect.any(Object),
            skipSynchronization: expect.any(Object),
            confidential: expect.any(Object),
            optionSet: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getDataElement', () => {
      it('should return NewDataElement for default DataElement initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDataElementFormGroup(sampleWithNewData);

        const dataElement = service.getDataElement(formGroup) as any;

        expect(dataElement).toMatchObject(sampleWithNewData);
      });

      it('should return NewDataElement for empty DataElement initial value', () => {
        const formGroup = service.createDataElementFormGroup();

        const dataElement = service.getDataElement(formGroup) as any;

        expect(dataElement).toMatchObject({});
      });

      it('should return IDataElement', () => {
        const formGroup = service.createDataElementFormGroup(sampleWithRequiredData);

        const dataElement = service.getDataElement(formGroup) as any;

        expect(dataElement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDataElement should not enable id FormControl', () => {
        const formGroup = service.createDataElementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDataElement should disable id FormControl', () => {
        const formGroup = service.createDataElementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
