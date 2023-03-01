import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sql-view.test-samples';

import { SqlViewFormService } from './sql-view-form.service';

describe('SqlView Form Service', () => {
  let service: SqlViewFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SqlViewFormService);
  });

  describe('Service methods', () => {
    describe('createSqlViewFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSqlViewFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            sqlQuery: expect.any(Object),
            type: expect.any(Object),
            cacheStrategy: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });

      it('passing ISqlView should create a new form with FormGroup', () => {
        const formGroup = service.createSqlViewFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            description: expect.any(Object),
            sqlQuery: expect.any(Object),
            type: expect.any(Object),
            cacheStrategy: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
          })
        );
      });
    });

    describe('getSqlView', () => {
      it('should return NewSqlView for default SqlView initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSqlViewFormGroup(sampleWithNewData);

        const sqlView = service.getSqlView(formGroup) as any;

        expect(sqlView).toMatchObject(sampleWithNewData);
      });

      it('should return NewSqlView for empty SqlView initial value', () => {
        const formGroup = service.createSqlViewFormGroup();

        const sqlView = service.getSqlView(formGroup) as any;

        expect(sqlView).toMatchObject({});
      });

      it('should return ISqlView', () => {
        const formGroup = service.createSqlViewFormGroup(sampleWithRequiredData);

        const sqlView = service.getSqlView(formGroup) as any;

        expect(sqlView).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISqlView should not enable id FormControl', () => {
        const formGroup = service.createSqlViewFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSqlView should disable id FormControl', () => {
        const formGroup = service.createSqlViewFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
