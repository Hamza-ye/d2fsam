import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../organisation-unit-group.test-samples';

import { OrganisationUnitGroupFormService } from './organisation-unit-group-form.service';

describe('OrganisationUnitGroup Form Service', () => {
  let service: OrganisationUnitGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrganisationUnitGroupFormService);
  });

  describe('Service methods', () => {
    describe('createOrganisationUnitGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrganisationUnitGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            shortName: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            symbol: expect.any(Object),
            color: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            members: expect.any(Object),
            groupSets: expect.any(Object),
          })
        );
      });

      it('passing IOrganisationUnitGroup should create a new form with FormGroup', () => {
        const formGroup = service.createOrganisationUnitGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            shortName: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            symbol: expect.any(Object),
            color: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            members: expect.any(Object),
            groupSets: expect.any(Object),
          })
        );
      });
    });

    describe('getOrganisationUnitGroup', () => {
      it('should return NewOrganisationUnitGroup for default OrganisationUnitGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrganisationUnitGroupFormGroup(sampleWithNewData);

        const organisationUnitGroup = service.getOrganisationUnitGroup(formGroup) as any;

        expect(organisationUnitGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrganisationUnitGroup for empty OrganisationUnitGroup initial value', () => {
        const formGroup = service.createOrganisationUnitGroupFormGroup();

        const organisationUnitGroup = service.getOrganisationUnitGroup(formGroup) as any;

        expect(organisationUnitGroup).toMatchObject({});
      });

      it('should return IOrganisationUnitGroup', () => {
        const formGroup = service.createOrganisationUnitGroupFormGroup(sampleWithRequiredData);

        const organisationUnitGroup = service.getOrganisationUnitGroup(formGroup) as any;

        expect(organisationUnitGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrganisationUnitGroup should not enable id FormControl', () => {
        const formGroup = service.createOrganisationUnitGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrganisationUnitGroup should disable id FormControl', () => {
        const formGroup = service.createOrganisationUnitGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
