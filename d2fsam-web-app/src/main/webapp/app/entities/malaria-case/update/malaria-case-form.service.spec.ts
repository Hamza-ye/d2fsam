import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../malaria-case.test-samples';

import { MalariaCaseFormService } from './malaria-case-form.service';

describe('MalariaCase Form Service', () => {
  let service: MalariaCaseFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MalariaCaseFormService);
  });

  describe('Service methods', () => {
    describe('createMalariaCaseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMalariaCaseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uuid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            entryStarted: expect.any(Object),
            lastSynced: expect.any(Object),
            deleted: expect.any(Object),
            dateOfExamination: expect.any(Object),
            mobile: expect.any(Object),
            gender: expect.any(Object),
            age: expect.any(Object),
            isPregnant: expect.any(Object),
            malariaTestResult: expect.any(Object),
            severity: expect.any(Object),
            referral: expect.any(Object),
            barImageUrl: expect.any(Object),
            comment: expect.any(Object),
            status: expect.any(Object),
            seen: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdAtClient: expect.any(Object),
            updatedAtClient: expect.any(Object),
            deletedAt: expect.any(Object),
            subVillage: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IMalariaCase should create a new form with FormGroup', () => {
        const formGroup = service.createMalariaCaseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uuid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            entryStarted: expect.any(Object),
            lastSynced: expect.any(Object),
            deleted: expect.any(Object),
            dateOfExamination: expect.any(Object),
            mobile: expect.any(Object),
            gender: expect.any(Object),
            age: expect.any(Object),
            isPregnant: expect.any(Object),
            malariaTestResult: expect.any(Object),
            severity: expect.any(Object),
            referral: expect.any(Object),
            barImageUrl: expect.any(Object),
            comment: expect.any(Object),
            status: expect.any(Object),
            seen: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            createdAtClient: expect.any(Object),
            updatedAtClient: expect.any(Object),
            deletedAt: expect.any(Object),
            subVillage: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getMalariaCase', () => {
      it('should return NewMalariaCase for default MalariaCase initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createMalariaCaseFormGroup(sampleWithNewData);

        const malariaCase = service.getMalariaCase(formGroup) as any;

        expect(malariaCase).toMatchObject(sampleWithNewData);
      });

      it('should return NewMalariaCase for empty MalariaCase initial value', () => {
        const formGroup = service.createMalariaCaseFormGroup();

        const malariaCase = service.getMalariaCase(formGroup) as any;

        expect(malariaCase).toMatchObject({});
      });

      it('should return IMalariaCase', () => {
        const formGroup = service.createMalariaCaseFormGroup(sampleWithRequiredData);

        const malariaCase = service.getMalariaCase(formGroup) as any;

        expect(malariaCase).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMalariaCase should not enable id FormControl', () => {
        const formGroup = service.createMalariaCaseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMalariaCase should disable id FormControl', () => {
        const formGroup = service.createMalariaCaseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
