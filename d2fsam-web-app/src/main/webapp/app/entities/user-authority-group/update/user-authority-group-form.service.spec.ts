import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-authority-group.test-samples';

import { UserAuthorityGroupFormService } from './user-authority-group-form.service';

describe('UserAuthorityGroup Form Service', () => {
  let service: UserAuthorityGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAuthorityGroupFormService);
  });

  describe('Service methods', () => {
    describe('createUserAuthorityGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserAuthorityGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            members: expect.any(Object),
          })
        );
      });

      it('passing IUserAuthorityGroup should create a new form with FormGroup', () => {
        const formGroup = service.createUserAuthorityGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            members: expect.any(Object),
          })
        );
      });
    });

    describe('getUserAuthorityGroup', () => {
      it('should return NewUserAuthorityGroup for default UserAuthorityGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserAuthorityGroupFormGroup(sampleWithNewData);

        const userAuthorityGroup = service.getUserAuthorityGroup(formGroup) as any;

        expect(userAuthorityGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserAuthorityGroup for empty UserAuthorityGroup initial value', () => {
        const formGroup = service.createUserAuthorityGroupFormGroup();

        const userAuthorityGroup = service.getUserAuthorityGroup(formGroup) as any;

        expect(userAuthorityGroup).toMatchObject({});
      });

      it('should return IUserAuthorityGroup', () => {
        const formGroup = service.createUserAuthorityGroupFormGroup(sampleWithRequiredData);

        const userAuthorityGroup = service.getUserAuthorityGroup(formGroup) as any;

        expect(userAuthorityGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserAuthorityGroup should not enable id FormControl', () => {
        const formGroup = service.createUserAuthorityGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserAuthorityGroup should disable id FormControl', () => {
        const formGroup = service.createUserAuthorityGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
