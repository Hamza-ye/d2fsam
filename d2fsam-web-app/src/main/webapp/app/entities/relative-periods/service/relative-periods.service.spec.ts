import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRelativePeriods } from '../relative-periods.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../relative-periods.test-samples';

import { RelativePeriodsService } from './relative-periods.service';

const requireRestSample: IRelativePeriods = {
  ...sampleWithRequiredData,
};

describe('RelativePeriods Service', () => {
  let service: RelativePeriodsService;
  let httpMock: HttpTestingController;
  let expectedResult: IRelativePeriods | IRelativePeriods[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RelativePeriodsService);
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

    it('should create a RelativePeriods', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const relativePeriods = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(relativePeriods).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RelativePeriods', () => {
      const relativePeriods = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(relativePeriods).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RelativePeriods', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RelativePeriods', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RelativePeriods', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRelativePeriodsToCollectionIfMissing', () => {
      it('should add a RelativePeriods to an empty array', () => {
        const relativePeriods: IRelativePeriods = sampleWithRequiredData;
        expectedResult = service.addRelativePeriodsToCollectionIfMissing([], relativePeriods);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relativePeriods);
      });

      it('should not add a RelativePeriods to an array that contains it', () => {
        const relativePeriods: IRelativePeriods = sampleWithRequiredData;
        const relativePeriodsCollection: IRelativePeriods[] = [
          {
            ...relativePeriods,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRelativePeriodsToCollectionIfMissing(relativePeriodsCollection, relativePeriods);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RelativePeriods to an array that doesn't contain it", () => {
        const relativePeriods: IRelativePeriods = sampleWithRequiredData;
        const relativePeriodsCollection: IRelativePeriods[] = [sampleWithPartialData];
        expectedResult = service.addRelativePeriodsToCollectionIfMissing(relativePeriodsCollection, relativePeriods);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relativePeriods);
      });

      it('should add only unique RelativePeriods to an array', () => {
        const relativePeriodsArray: IRelativePeriods[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const relativePeriodsCollection: IRelativePeriods[] = [sampleWithRequiredData];
        expectedResult = service.addRelativePeriodsToCollectionIfMissing(relativePeriodsCollection, ...relativePeriodsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const relativePeriods: IRelativePeriods = sampleWithRequiredData;
        const relativePeriods2: IRelativePeriods = sampleWithPartialData;
        expectedResult = service.addRelativePeriodsToCollectionIfMissing([], relativePeriods, relativePeriods2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relativePeriods);
        expect(expectedResult).toContain(relativePeriods2);
      });

      it('should accept null and undefined values', () => {
        const relativePeriods: IRelativePeriods = sampleWithRequiredData;
        expectedResult = service.addRelativePeriodsToCollectionIfMissing([], null, relativePeriods, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relativePeriods);
      });

      it('should return initial array if no RelativePeriods is added', () => {
        const relativePeriodsCollection: IRelativePeriods[] = [sampleWithRequiredData];
        expectedResult = service.addRelativePeriodsToCollectionIfMissing(relativePeriodsCollection, undefined, null);
        expect(expectedResult).toEqual(relativePeriodsCollection);
      });
    });

    describe('compareRelativePeriods', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRelativePeriods(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRelativePeriods(entity1, entity2);
        const compareResult2 = service.compareRelativePeriods(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRelativePeriods(entity1, entity2);
        const compareResult2 = service.compareRelativePeriods(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRelativePeriods(entity1, entity2);
        const compareResult2 = service.compareRelativePeriods(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
