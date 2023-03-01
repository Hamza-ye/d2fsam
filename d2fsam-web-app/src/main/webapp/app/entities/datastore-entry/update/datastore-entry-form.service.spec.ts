import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../datastore-entry.test-samples';

import { DatastoreEntryFormService } from './datastore-entry-form.service';

describe('DatastoreEntry Form Service', () => {
  let service: DatastoreEntryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DatastoreEntryFormService);
  });

  describe('Service methods', () => {
    describe('createDatastoreEntryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDatastoreEntryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            key: expect.any(Object),
            namespace: expect.any(Object),
            encrypted: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing IDatastoreEntry should create a new form with FormGroup', () => {
        const formGroup = service.createDatastoreEntryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            key: expect.any(Object),
            namespace: expect.any(Object),
            encrypted: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getDatastoreEntry', () => {
      it('should return NewDatastoreEntry for default DatastoreEntry initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDatastoreEntryFormGroup(sampleWithNewData);

        const datastoreEntry = service.getDatastoreEntry(formGroup) as any;

        expect(datastoreEntry).toMatchObject(sampleWithNewData);
      });

      it('should return NewDatastoreEntry for empty DatastoreEntry initial value', () => {
        const formGroup = service.createDatastoreEntryFormGroup();

        const datastoreEntry = service.getDatastoreEntry(formGroup) as any;

        expect(datastoreEntry).toMatchObject({});
      });

      it('should return IDatastoreEntry', () => {
        const formGroup = service.createDatastoreEntryFormGroup(sampleWithRequiredData);

        const datastoreEntry = service.getDatastoreEntry(formGroup) as any;

        expect(datastoreEntry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDatastoreEntry should not enable id FormControl', () => {
        const formGroup = service.createDatastoreEntryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDatastoreEntry should disable id FormControl', () => {
        const formGroup = service.createDatastoreEntryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
