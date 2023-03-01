import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IStockItemGroup } from '../stock-item-group.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../stock-item-group.test-samples';

import { StockItemGroupService, RestStockItemGroup } from './stock-item-group.service';

const requireRestSample: RestStockItemGroup = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('StockItemGroup Service', () => {
  let service: StockItemGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: IStockItemGroup | IStockItemGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StockItemGroupService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a StockItemGroup', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const stockItemGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stockItemGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StockItemGroup', () => {
      const stockItemGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stockItemGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StockItemGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StockItemGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StockItemGroup', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStockItemGroupToCollectionIfMissing', () => {
      it('should add a StockItemGroup to an empty array', () => {
        const stockItemGroup: IStockItemGroup = sampleWithRequiredData;
        expectedResult = service.addStockItemGroupToCollectionIfMissing([], stockItemGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stockItemGroup);
      });

      it('should not add a StockItemGroup to an array that contains it', () => {
        const stockItemGroup: IStockItemGroup = sampleWithRequiredData;
        const stockItemGroupCollection: IStockItemGroup[] = [
          {
            ...stockItemGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStockItemGroupToCollectionIfMissing(stockItemGroupCollection, stockItemGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StockItemGroup to an array that doesn't contain it", () => {
        const stockItemGroup: IStockItemGroup = sampleWithRequiredData;
        const stockItemGroupCollection: IStockItemGroup[] = [sampleWithPartialData];
        expectedResult = service.addStockItemGroupToCollectionIfMissing(stockItemGroupCollection, stockItemGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockItemGroup);
      });

      it('should add only unique StockItemGroup to an array', () => {
        const stockItemGroupArray: IStockItemGroup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stockItemGroupCollection: IStockItemGroup[] = [sampleWithRequiredData];
        expectedResult = service.addStockItemGroupToCollectionIfMissing(stockItemGroupCollection, ...stockItemGroupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stockItemGroup: IStockItemGroup = sampleWithRequiredData;
        const stockItemGroup2: IStockItemGroup = sampleWithPartialData;
        expectedResult = service.addStockItemGroupToCollectionIfMissing([], stockItemGroup, stockItemGroup2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockItemGroup);
        expect(expectedResult).toContain(stockItemGroup2);
      });

      it('should accept null and undefined values', () => {
        const stockItemGroup: IStockItemGroup = sampleWithRequiredData;
        expectedResult = service.addStockItemGroupToCollectionIfMissing([], null, stockItemGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stockItemGroup);
      });

      it('should return initial array if no StockItemGroup is added', () => {
        const stockItemGroupCollection: IStockItemGroup[] = [sampleWithRequiredData];
        expectedResult = service.addStockItemGroupToCollectionIfMissing(stockItemGroupCollection, undefined, null);
        expect(expectedResult).toEqual(stockItemGroupCollection);
      });
    });

    describe('compareStockItemGroup', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStockItemGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareStockItemGroup(entity1, entity2);
        const compareResult2 = service.compareStockItemGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareStockItemGroup(entity1, entity2);
        const compareResult2 = service.compareStockItemGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareStockItemGroup(entity1, entity2);
        const compareResult2 = service.compareStockItemGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
