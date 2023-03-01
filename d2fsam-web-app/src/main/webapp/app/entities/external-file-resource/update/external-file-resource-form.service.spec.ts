import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../external-file-resource.test-samples';

import { ExternalFileResourceFormService } from './external-file-resource-form.service';

describe('ExternalFileResource Form Service', () => {
  let service: ExternalFileResourceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExternalFileResourceFormService);
  });

  describe('Service methods', () => {
    describe('createExternalFileResourceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExternalFileResourceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            accessToken: expect.any(Object),
            expires: expect.any(Object),
            fileResource: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IExternalFileResource should create a new form with FormGroup', () => {
        const formGroup = service.createExternalFileResourceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            accessToken: expect.any(Object),
            expires: expect.any(Object),
            fileResource: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getExternalFileResource', () => {
      it('should return NewExternalFileResource for default ExternalFileResource initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createExternalFileResourceFormGroup(sampleWithNewData);

        const externalFileResource = service.getExternalFileResource(formGroup) as any;

        expect(externalFileResource).toMatchObject(sampleWithNewData);
      });

      it('should return NewExternalFileResource for empty ExternalFileResource initial value', () => {
        const formGroup = service.createExternalFileResourceFormGroup();

        const externalFileResource = service.getExternalFileResource(formGroup) as any;

        expect(externalFileResource).toMatchObject({});
      });

      it('should return IExternalFileResource', () => {
        const formGroup = service.createExternalFileResourceFormGroup(sampleWithRequiredData);

        const externalFileResource = service.getExternalFileResource(formGroup) as any;

        expect(externalFileResource).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExternalFileResource should not enable id FormControl', () => {
        const formGroup = service.createExternalFileResourceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExternalFileResource should disable id FormControl', () => {
        const formGroup = service.createExternalFileResourceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
