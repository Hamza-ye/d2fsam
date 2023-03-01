import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-group.test-samples';

import { UserGroupFormService } from './user-group-form.service';

describe('UserGroup Form Service', () => {
  let service: UserGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserGroupFormService);
  });

  describe('Service methods', () => {
    describe('createUserGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            uuid: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            activeFrom: expect.any(Object),
            activeTo: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            members: expect.any(Object),
            managedGroups: expect.any(Object),
            managedByGroups: expect.any(Object),
          })
        );
      });

      it('passing IUserGroup should create a new form with FormGroup', () => {
        const formGroup = service.createUserGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            uuid: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            activeFrom: expect.any(Object),
            activeTo: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            members: expect.any(Object),
            managedGroups: expect.any(Object),
            managedByGroups: expect.any(Object),
          })
        );
      });
    });

    describe('getUserGroup', () => {
      it('should return NewUserGroup for default UserGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserGroupFormGroup(sampleWithNewData);

        const userGroup = service.getUserGroup(formGroup) as any;

        expect(userGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserGroup for empty UserGroup initial value', () => {
        const formGroup = service.createUserGroupFormGroup();

        const userGroup = service.getUserGroup(formGroup) as any;

        expect(userGroup).toMatchObject({});
      });

      it('should return IUserGroup', () => {
        const formGroup = service.createUserGroupFormGroup(sampleWithRequiredData);

        const userGroup = service.getUserGroup(formGroup) as any;

        expect(userGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserGroup should not enable id FormControl', () => {
        const formGroup = service.createUserGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserGroup should disable id FormControl', () => {
        const formGroup = service.createUserGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
