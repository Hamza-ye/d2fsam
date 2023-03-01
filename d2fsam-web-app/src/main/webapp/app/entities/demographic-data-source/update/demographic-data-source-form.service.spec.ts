import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../demographic-data-source.test-samples';

import { DemographicDataSourceFormService } from './demographic-data-source-form.service';

describe('DemographicDataSource Form Service', () => {
  let service: DemographicDataSourceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DemographicDataSourceFormService);
  });

  describe('Service methods', () => {
    describe('createDemographicDataSourceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDemographicDataSourceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IDemographicDataSource should create a new form with FormGroup', () => {
        const formGroup = service.createDemographicDataSourceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getDemographicDataSource', () => {
      it('should return NewDemographicDataSource for default DemographicDataSource initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDemographicDataSourceFormGroup(sampleWithNewData);

        const demographicDataSource = service.getDemographicDataSource(formGroup) as any;

        expect(demographicDataSource).toMatchObject(sampleWithNewData);
      });

      it('should return NewDemographicDataSource for empty DemographicDataSource initial value', () => {
        const formGroup = service.createDemographicDataSourceFormGroup();

        const demographicDataSource = service.getDemographicDataSource(formGroup) as any;

        expect(demographicDataSource).toMatchObject({});
      });

      it('should return IDemographicDataSource', () => {
        const formGroup = service.createDemographicDataSourceFormGroup(sampleWithRequiredData);

        const demographicDataSource = service.getDemographicDataSource(formGroup) as any;

        expect(demographicDataSource).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDemographicDataSource should not enable id FormControl', () => {
        const formGroup = service.createDemographicDataSourceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDemographicDataSource should disable id FormControl', () => {
        const formGroup = service.createDemographicDataSourceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
