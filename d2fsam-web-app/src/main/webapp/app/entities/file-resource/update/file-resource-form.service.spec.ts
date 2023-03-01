import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../file-resource.test-samples';

import { FileResourceFormService } from './file-resource-form.service';

describe('FileResource Form Service', () => {
  let service: FileResourceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileResourceFormService);
  });

  describe('Service methods', () => {
    describe('createFileResourceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFileResourceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            contentType: expect.any(Object),
            contentLength: expect.any(Object),
            contentMd5: expect.any(Object),
            storageKey: expect.any(Object),
            assigned: expect.any(Object),
            domain: expect.any(Object),
            hasMultipleStorageFiles: expect.any(Object),
            fileResourceOwner: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IFileResource should create a new form with FormGroup', () => {
        const formGroup = service.createFileResourceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            contentType: expect.any(Object),
            contentLength: expect.any(Object),
            contentMd5: expect.any(Object),
            storageKey: expect.any(Object),
            assigned: expect.any(Object),
            domain: expect.any(Object),
            hasMultipleStorageFiles: expect.any(Object),
            fileResourceOwner: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getFileResource', () => {
      it('should return NewFileResource for default FileResource initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFileResourceFormGroup(sampleWithNewData);

        const fileResource = service.getFileResource(formGroup) as any;

        expect(fileResource).toMatchObject(sampleWithNewData);
      });

      it('should return NewFileResource for empty FileResource initial value', () => {
        const formGroup = service.createFileResourceFormGroup();

        const fileResource = service.getFileResource(formGroup) as any;

        expect(fileResource).toMatchObject({});
      });

      it('should return IFileResource', () => {
        const formGroup = service.createFileResourceFormGroup(sampleWithRequiredData);

        const fileResource = service.getFileResource(formGroup) as any;

        expect(fileResource).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFileResource should not enable id FormControl', () => {
        const formGroup = service.createFileResourceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFileResource should disable id FormControl', () => {
        const formGroup = service.createFileResourceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
