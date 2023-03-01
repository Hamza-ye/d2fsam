import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../organisation-unit.test-samples';

import { OrganisationUnitFormService } from './organisation-unit-form.service';

describe('OrganisationUnit Form Service', () => {
  let service: OrganisationUnitFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrganisationUnitFormService);
  });

  describe('Service methods', () => {
    describe('createOrganisationUnitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrganisationUnitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            shortName: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            path: expect.any(Object),
            hierarchyLevel: expect.any(Object),
            openingDate: expect.any(Object),
            comment: expect.any(Object),
            closedDate: expect.any(Object),
            url: expect.any(Object),
            contactPerson: expect.any(Object),
            address: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            organisationUnitType: expect.any(Object),
            inactive: expect.any(Object),
            parent: expect.any(Object),
            hfHomeSubVillage: expect.any(Object),
            servicingHf: expect.any(Object),
            image: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            assignedChv: expect.any(Object),
            programs: expect.any(Object),
            targetedInActivities: expect.any(Object),
            groups: expect.any(Object),
            users: expect.any(Object),
            searchingUsers: expect.any(Object),
            dataViewUsers: expect.any(Object),
          })
        );
      });

      it('passing IOrganisationUnit should create a new form with FormGroup', () => {
        const formGroup = service.createOrganisationUnitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            shortName: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            path: expect.any(Object),
            hierarchyLevel: expect.any(Object),
            openingDate: expect.any(Object),
            comment: expect.any(Object),
            closedDate: expect.any(Object),
            url: expect.any(Object),
            contactPerson: expect.any(Object),
            address: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            organisationUnitType: expect.any(Object),
            inactive: expect.any(Object),
            parent: expect.any(Object),
            hfHomeSubVillage: expect.any(Object),
            servicingHf: expect.any(Object),
            image: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            assignedChv: expect.any(Object),
            programs: expect.any(Object),
            targetedInActivities: expect.any(Object),
            groups: expect.any(Object),
            users: expect.any(Object),
            searchingUsers: expect.any(Object),
            dataViewUsers: expect.any(Object),
          })
        );
      });
    });

    describe('getOrganisationUnit', () => {
      it('should return NewOrganisationUnit for default OrganisationUnit initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrganisationUnitFormGroup(sampleWithNewData);

        const organisationUnit = service.getOrganisationUnit(formGroup) as any;

        expect(organisationUnit).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrganisationUnit for empty OrganisationUnit initial value', () => {
        const formGroup = service.createOrganisationUnitFormGroup();

        const organisationUnit = service.getOrganisationUnit(formGroup) as any;

        expect(organisationUnit).toMatchObject({});
      });

      it('should return IOrganisationUnit', () => {
        const formGroup = service.createOrganisationUnitFormGroup(sampleWithRequiredData);

        const organisationUnit = service.getOrganisationUnit(formGroup) as any;

        expect(organisationUnit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrganisationUnit should not enable id FormControl', () => {
        const formGroup = service.createOrganisationUnitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrganisationUnit should disable id FormControl', () => {
        const formGroup = service.createOrganisationUnitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
