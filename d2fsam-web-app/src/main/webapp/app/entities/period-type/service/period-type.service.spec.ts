import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPeriodType } from '../period-type.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../period-type.test-samples';

import { PeriodTypeService } from './period-type.service';

const requireRestSample: IPeriodType = {
  ...sampleWithRequiredData,
};

describe('PeriodType Service', () => {
  let service: PeriodTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IPeriodType | IPeriodType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PeriodTypeService);
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

    it('should create a PeriodType', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const periodType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(periodType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PeriodType', () => {
      const periodType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(periodType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PeriodType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PeriodType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PeriodType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPeriodTypeToCollectionIfMissing', () => {
      it('should add a PeriodType to an empty array', () => {
        const periodType: IPeriodType = sampleWithRequiredData;
        expectedResult = service.addPeriodTypeToCollectionIfMissing([], periodType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(periodType);
      });

      it('should not add a PeriodType to an array that contains it', () => {
        const periodType: IPeriodType = sampleWithRequiredData;
        const periodTypeCollection: IPeriodType[] = [
          {
            ...periodType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPeriodTypeToCollectionIfMissing(periodTypeCollection, periodType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PeriodType to an array that doesn't contain it", () => {
        const periodType: IPeriodType = sampleWithRequiredData;
        const periodTypeCollection: IPeriodType[] = [sampleWithPartialData];
        expectedResult = service.addPeriodTypeToCollectionIfMissing(periodTypeCollection, periodType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(periodType);
      });

      it('should add only unique PeriodType to an array', () => {
        const periodTypeArray: IPeriodType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const periodTypeCollection: IPeriodType[] = [sampleWithRequiredData];
        expectedResult = service.addPeriodTypeToCollectionIfMissing(periodTypeCollection, ...periodTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const periodType: IPeriodType = sampleWithRequiredData;
        const periodType2: IPeriodType = sampleWithPartialData;
        expectedResult = service.addPeriodTypeToCollectionIfMissing([], periodType, periodType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(periodType);
        expect(expectedResult).toContain(periodType2);
      });

      it('should accept null and undefined values', () => {
        const periodType: IPeriodType = sampleWithRequiredData;
        expectedResult = service.addPeriodTypeToCollectionIfMissing([], null, periodType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(periodType);
      });

      it('should return initial array if no PeriodType is added', () => {
        const periodTypeCollection: IPeriodType[] = [sampleWithRequiredData];
        expectedResult = service.addPeriodTypeToCollectionIfMissing(periodTypeCollection, undefined, null);
        expect(expectedResult).toEqual(periodTypeCollection);
      });
    });

    describe('comparePeriodType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePeriodType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePeriodType(entity1, entity2);
        const compareResult2 = service.comparePeriodType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePeriodType(entity1, entity2);
        const compareResult2 = service.comparePeriodType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePeriodType(entity1, entity2);
        const compareResult2 = service.comparePeriodType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
