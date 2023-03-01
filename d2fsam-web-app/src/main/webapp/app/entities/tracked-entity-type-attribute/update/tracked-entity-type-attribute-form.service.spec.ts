import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tracked-entity-type-attribute.test-samples';

import { TrackedEntityTypeAttributeFormService } from './tracked-entity-type-attribute-form.service';

describe('TrackedEntityTypeAttribute Form Service', () => {
  let service: TrackedEntityTypeAttributeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackedEntityTypeAttributeFormService);
  });

  describe('Service methods', () => {
    describe('createTrackedEntityTypeAttributeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            displayInList: expect.any(Object),
            mandatory: expect.any(Object),
            searchable: expect.any(Object),
            trackedEntityAttribute: expect.any(Object),
            trackedEntityType: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing ITrackedEntityTypeAttribute should create a new form with FormGroup', () => {
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            displayInList: expect.any(Object),
            mandatory: expect.any(Object),
            searchable: expect.any(Object),
            trackedEntityAttribute: expect.any(Object),
            trackedEntityType: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getTrackedEntityTypeAttribute', () => {
      it('should return NewTrackedEntityTypeAttribute for default TrackedEntityTypeAttribute initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup(sampleWithNewData);

        const trackedEntityTypeAttribute = service.getTrackedEntityTypeAttribute(formGroup) as any;

        expect(trackedEntityTypeAttribute).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackedEntityTypeAttribute for empty TrackedEntityTypeAttribute initial value', () => {
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup();

        const trackedEntityTypeAttribute = service.getTrackedEntityTypeAttribute(formGroup) as any;

        expect(trackedEntityTypeAttribute).toMatchObject({});
      });

      it('should return ITrackedEntityTypeAttribute', () => {
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup(sampleWithRequiredData);

        const trackedEntityTypeAttribute = service.getTrackedEntityTypeAttribute(formGroup) as any;

        expect(trackedEntityTypeAttribute).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackedEntityTypeAttribute should not enable id FormControl', () => {
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackedEntityTypeAttribute should disable id FormControl', () => {
        const formGroup = service.createTrackedEntityTypeAttributeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
