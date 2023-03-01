import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../organisation-unit-level.test-samples';

import { OrganisationUnitLevelFormService } from './organisation-unit-level-form.service';

describe('OrganisationUnitLevel Form Service', () => {
  let service: OrganisationUnitLevelFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrganisationUnitLevelFormService);
  });

  describe('Service methods', () => {
    describe('createOrganisationUnitLevelFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrganisationUnitLevelFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            level: expect.any(Object),
            offlineLevels: expect.any(Object),
          })
        );
      });

      it('passing IOrganisationUnitLevel should create a new form with FormGroup', () => {
        const formGroup = service.createOrganisationUnitLevelFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            level: expect.any(Object),
            offlineLevels: expect.any(Object),
          })
        );
      });
    });

    describe('getOrganisationUnitLevel', () => {
      it('should return NewOrganisationUnitLevel for default OrganisationUnitLevel initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrganisationUnitLevelFormGroup(sampleWithNewData);

        const organisationUnitLevel = service.getOrganisationUnitLevel(formGroup) as any;

        expect(organisationUnitLevel).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrganisationUnitLevel for empty OrganisationUnitLevel initial value', () => {
        const formGroup = service.createOrganisationUnitLevelFormGroup();

        const organisationUnitLevel = service.getOrganisationUnitLevel(formGroup) as any;

        expect(organisationUnitLevel).toMatchObject({});
      });

      it('should return IOrganisationUnitLevel', () => {
        const formGroup = service.createOrganisationUnitLevelFormGroup(sampleWithRequiredData);

        const organisationUnitLevel = service.getOrganisationUnitLevel(formGroup) as any;

        expect(organisationUnitLevel).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrganisationUnitLevel should not enable id FormControl', () => {
        const formGroup = service.createOrganisationUnitLevelFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrganisationUnitLevel should disable id FormControl', () => {
        const formGroup = service.createOrganisationUnitLevelFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
