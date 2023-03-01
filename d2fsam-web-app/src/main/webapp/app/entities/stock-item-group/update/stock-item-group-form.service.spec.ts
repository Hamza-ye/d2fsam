import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../stock-item-group.test-samples';

import { StockItemGroupFormService } from './stock-item-group-form.service';

describe('StockItemGroup Form Service', () => {
  let service: StockItemGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StockItemGroupFormService);
  });

  describe('Service methods', () => {
    describe('createStockItemGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStockItemGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            shortName: expect.any(Object),
            description: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            items: expect.any(Object),
          })
        );
      });

      it('passing IStockItemGroup should create a new form with FormGroup', () => {
        const formGroup = service.createStockItemGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uid: expect.any(Object),
            code: expect.any(Object),
            name: expect.any(Object),
            shortName: expect.any(Object),
            description: expect.any(Object),
            created: expect.any(Object),
            updated: expect.any(Object),
            inactive: expect.any(Object),
            createdBy: expect.any(Object),
            updatedBy: expect.any(Object),
            items: expect.any(Object),
          })
        );
      });
    });

    describe('getStockItemGroup', () => {
      it('should return NewStockItemGroup for default StockItemGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createStockItemGroupFormGroup(sampleWithNewData);

        const stockItemGroup = service.getStockItemGroup(formGroup) as any;

        expect(stockItemGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewStockItemGroup for empty StockItemGroup initial value', () => {
        const formGroup = service.createStockItemGroupFormGroup();

        const stockItemGroup = service.getStockItemGroup(formGroup) as any;

        expect(stockItemGroup).toMatchObject({});
      });

      it('should return IStockItemGroup', () => {
        const formGroup = service.createStockItemGroupFormGroup(sampleWithRequiredData);

        const stockItemGroup = service.getStockItemGroup(formGroup) as any;

        expect(stockItemGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStockItemGroup should not enable id FormControl', () => {
        const formGroup = service.createStockItemGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStockItemGroup should disable id FormControl', () => {
        const formGroup = service.createStockItemGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
