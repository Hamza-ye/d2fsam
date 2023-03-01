import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../demographic-data.test-samples';

import { DemographicDataFormService } from './demographic-data-form.service';

describe('DemographicData Form Service', () => {
  let service: DemographicDataFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DemographicDataFormService);
  });

  describe('Service methods', () => {
    describe('createDemographicDataFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDemographicDataFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            date: expect.any(Object),
            level: expect.any(Object),
            totalPopulation: expect.any(Object),
            malePopulation: expect.any(Object),
            femalePopulation: expect.any(Object),
            lessThan5Population: expect.any(Object),
            greaterThan5Population: expect.any(Object),
            bw5And15Population: expect.any(Object),
            greaterThan15Population: expect.any(Object),
            households: expect.any(Object),
            houses: expect.any(Object),
            healthFacilities: expect.any(Object),
            avgNoOfRooms: expect.any(Object),
            avgRoomArea: expect.any(Object),
            avgHouseArea: expect.any(Object),
            individualsPerHousehold: expect.any(Object),
            populationGrowthRate: expect.any(Object),
            comment: expect.any(Object),
            organisationUnit: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            source: expect.any(Object),
          })
        );
      });

      it('passing IDemographicData should create a new form with FormGroup', () => {
        const formGroup = service.createDemographicDataFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            date: expect.any(Object),
            level: expect.any(Object),
            totalPopulation: expect.any(Object),
            malePopulation: expect.any(Object),
            femalePopulation: expect.any(Object),
            lessThan5Population: expect.any(Object),
            greaterThan5Population: expect.any(Object),
            bw5And15Population: expect.any(Object),
            greaterThan15Population: expect.any(Object),
            households: expect.any(Object),
            houses: expect.any(Object),
            healthFacilities: expect.any(Object),
            avgNoOfRooms: expect.any(Object),
            avgRoomArea: expect.any(Object),
            avgHouseArea: expect.any(Object),
            individualsPerHousehold: expect.any(Object),
            populationGrowthRate: expect.any(Object),
            comment: expect.any(Object),
            organisationUnit: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            source: expect.any(Object),
          })
        );
      });
    });

    describe('getDemographicData', () => {
      it('should return NewDemographicData for default DemographicData initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDemographicDataFormGroup(sampleWithNewData);

        const demographicData = service.getDemographicData(formGroup) as any;

        expect(demographicData).toMatchObject(sampleWithNewData);
      });

      it('should return NewDemographicData for empty DemographicData initial value', () => {
        const formGroup = service.createDemographicDataFormGroup();

        const demographicData = service.getDemographicData(formGroup) as any;

        expect(demographicData).toMatchObject({});
      });

      it('should return IDemographicData', () => {
        const formGroup = service.createDemographicDataFormGroup(sampleWithRequiredData);

        const demographicData = service.getDemographicData(formGroup) as any;

        expect(demographicData).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDemographicData should not enable id FormControl', () => {
        const formGroup = service.createDemographicDataFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDemographicData should disable id FormControl', () => {
        const formGroup = service.createDemographicDataFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
