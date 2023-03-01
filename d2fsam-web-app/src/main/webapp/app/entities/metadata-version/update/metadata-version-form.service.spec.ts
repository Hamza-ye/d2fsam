import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../metadata-version.test-samples';

import { MetadataVersionFormService } from './metadata-version-form.service';

describe('MetadataVersion Form Service', () => {
  let service: MetadataVersionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MetadataVersionFormService);
  });

  describe('Service methods', () => {
    describe('createMetadataVersionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMetadataVersionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            importDate: expect.any(Object),
            type: expect.any(Object),
            hashCode: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IMetadataVersion should create a new form with FormGroup', () => {
        const formGroup = service.createMetadataVersionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            importDate: expect.any(Object),
            type: expect.any(Object),
            hashCode: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getMetadataVersion', () => {
      it('should return NewMetadataVersion for default MetadataVersion initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createMetadataVersionFormGroup(sampleWithNewData);

        const metadataVersion = service.getMetadataVersion(formGroup) as any;

        expect(metadataVersion).toMatchObject(sampleWithNewData);
      });

      it('should return NewMetadataVersion for empty MetadataVersion initial value', () => {
        const formGroup = service.createMetadataVersionFormGroup();

        const metadataVersion = service.getMetadataVersion(formGroup) as any;

        expect(metadataVersion).toMatchObject({});
      });

      it('should return IMetadataVersion', () => {
        const formGroup = service.createMetadataVersionFormGroup(sampleWithRequiredData);

        const metadataVersion = service.getMetadataVersion(formGroup) as any;

        expect(metadataVersion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMetadataVersion should not enable id FormControl', () => {
        const formGroup = service.createMetadataVersionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMetadataVersion should disable id FormControl', () => {
        const formGroup = service.createMetadataVersionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
