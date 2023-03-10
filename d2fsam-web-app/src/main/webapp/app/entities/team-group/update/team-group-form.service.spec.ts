import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../team-group.test-samples';

import { TeamGroupFormService } from './team-group-form.service';

describe('TeamGroup Form Service', () => {
  let service: TeamGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamGroupFormService);
  });

  describe('Service methods', () => {
    describe('createTeamGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamGroupFormGroup();

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
            members: expect.any(Object),
          })
        );
      });

      it('passing ITeamGroup should create a new form with FormGroup', () => {
        const formGroup = service.createTeamGroupFormGroup(sampleWithRequiredData);

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
            members: expect.any(Object),
          })
        );
      });
    });

    describe('getTeamGroup', () => {
      it('should return NewTeamGroup for default TeamGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTeamGroupFormGroup(sampleWithNewData);

        const teamGroup = service.getTeamGroup(formGroup) as any;

        expect(teamGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamGroup for empty TeamGroup initial value', () => {
        const formGroup = service.createTeamGroupFormGroup();

        const teamGroup = service.getTeamGroup(formGroup) as any;

        expect(teamGroup).toMatchObject({});
      });

      it('should return ITeamGroup', () => {
        const formGroup = service.createTeamGroupFormGroup(sampleWithRequiredData);

        const teamGroup = service.getTeamGroup(formGroup) as any;

        expect(teamGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamGroup should not enable id FormControl', () => {
        const formGroup = service.createTeamGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamGroup should disable id FormControl', () => {
        const formGroup = service.createTeamGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
