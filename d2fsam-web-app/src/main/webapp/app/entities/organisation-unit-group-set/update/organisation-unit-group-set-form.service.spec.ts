import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../organisation-unit-group-set.test-samples';

import { OrganisationUnitGroupSetFormService } from './organisation-unit-group-set-form.service';

describe('OrganisationUnitGroupSet Form Service', () => {
  let service: OrganisationUnitGroupSetFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrganisationUnitGroupSetFormService);
  });

  describe('Service methods', () => {
    describe('createOrganisationUnitGroupSetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrganisationUnitGroupSetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            compulsory: expect.any(Object),
            includeSubhierarchyInAnalytics: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            organisationUnitGroups: expect.any(Object),
          })
        );
      });

      it('passing IOrganisationUnitGroupSet should create a new form with FormGroup', () => {
        const formGroup = service.createOrganisationUnitGroupSetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            compulsory: expect.any(Object),
            includeSubhierarchyInAnalytics: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            organisationUnitGroups: expect.any(Object),
          })
        );
      });
    });

    describe('getOrganisationUnitGroupSet', () => {
      it('should return NewOrganisationUnitGroupSet for default OrganisationUnitGroupSet initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrganisationUnitGroupSetFormGroup(sampleWithNewData);

        const organisationUnitGroupSet = service.getOrganisationUnitGroupSet(formGroup) as any;

        expect(organisationUnitGroupSet).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrganisationUnitGroupSet for empty OrganisationUnitGroupSet initial value', () => {
        const formGroup = service.createOrganisationUnitGroupSetFormGroup();

        const organisationUnitGroupSet = service.getOrganisationUnitGroupSet(formGroup) as any;

        expect(organisationUnitGroupSet).toMatchObject({});
      });

      it('should return IOrganisationUnitGroupSet', () => {
        const formGroup = service.createOrganisationUnitGroupSetFormGroup(sampleWithRequiredData);

        const organisationUnitGroupSet = service.getOrganisationUnitGroupSet(formGroup) as any;

        expect(organisationUnitGroupSet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrganisationUnitGroupSet should not enable id FormControl', () => {
        const formGroup = service.createOrganisationUnitGroupSetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrganisationUnitGroupSet should disable id FormControl', () => {
        const formGroup = service.createOrganisationUnitGroupSetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
