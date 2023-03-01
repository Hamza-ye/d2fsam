import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../chv.test-samples';

import { ChvFormService } from './chv-form.service';

describe('Chv Form Service', () => {
  let service: ChvFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChvFormService);
  });

  describe('Service methods', () => {
    describe('createChvFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createChvFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            withdrawn: expect.any(Object),
            dateJoined: expect.any(Object),
            dateWithdrawn: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            inactive: expect.any(Object),
            assignedTo: expect.any(Object),
            district: expect.any(Object),
            homeSubvillage: expect.any(Object),
            managingHf: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IChv should create a new form with FormGroup', () => {
        const formGroup = service.createChvFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            withdrawn: expect.any(Object),
            dateJoined: expect.any(Object),
            dateWithdrawn: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            inactive: expect.any(Object),
            assignedTo: expect.any(Object),
            district: expect.any(Object),
            homeSubvillage: expect.any(Object),
            managingHf: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getChv', () => {
      it('should return NewChv for default Chv initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createChvFormGroup(sampleWithNewData);

        const chv = service.getChv(formGroup) as any;

        expect(chv).toMatchObject(sampleWithNewData);
      });

      it('should return NewChv for empty Chv initial value', () => {
        const formGroup = service.createChvFormGroup();

        const chv = service.getChv(formGroup) as any;

        expect(chv).toMatchObject({});
      });

      it('should return IChv', () => {
        const formGroup = service.createChvFormGroup(sampleWithRequiredData);

        const chv = service.getChv(formGroup) as any;

        expect(chv).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IChv should not enable id FormControl', () => {
        const formGroup = service.createChvFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewChv should disable id FormControl', () => {
        const formGroup = service.createChvFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
