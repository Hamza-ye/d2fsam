import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPeriod } from '../period.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../period.test-samples';

import { PeriodService, RestPeriod } from './period.service';

const requireRestSample: RestPeriod = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.format(DATE_FORMAT),
  endDate: sampleWithRequiredData.endDate?.format(DATE_FORMAT),
};

describe('Period Service', () => {
  let service: PeriodService;
  let httpMock: HttpTestingController;
  let expectedResult: IPeriod | IPeriod[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PeriodService);
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

    it('should create a Period', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const period = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(period).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Period', () => {
      const period = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(period).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Period', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Period', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Period', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPeriodToCollectionIfMissing', () => {
      it('should add a Period to an empty array', () => {
        const period: IPeriod = sampleWithRequiredData;
        expectedResult = service.addPeriodToCollectionIfMissing([], period);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(period);
      });

      it('should not add a Period to an array that contains it', () => {
        const period: IPeriod = sampleWithRequiredData;
        const periodCollection: IPeriod[] = [
          {
            ...period,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPeriodToCollectionIfMissing(periodCollection, period);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Period to an array that doesn't contain it", () => {
        const period: IPeriod = sampleWithRequiredData;
        const periodCollection: IPeriod[] = [sampleWithPartialData];
        expectedResult = service.addPeriodToCollectionIfMissing(periodCollection, period);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(period);
      });

      it('should add only unique Period to an array', () => {
        const periodArray: IPeriod[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const periodCollection: IPeriod[] = [sampleWithRequiredData];
        expectedResult = service.addPeriodToCollectionIfMissing(periodCollection, ...periodArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const period: IPeriod = sampleWithRequiredData;
        const period2: IPeriod = sampleWithPartialData;
        expectedResult = service.addPeriodToCollectionIfMissing([], period, period2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(period);
        expect(expectedResult).toContain(period2);
      });

      it('should accept null and undefined values', () => {
        const period: IPeriod = sampleWithRequiredData;
        expectedResult = service.addPeriodToCollectionIfMissing([], null, period, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(period);
      });

      it('should return initial array if no Period is added', () => {
        const periodCollection: IPeriod[] = [sampleWithRequiredData];
        expectedResult = service.addPeriodToCollectionIfMissing(periodCollection, undefined, null);
        expect(expectedResult).toEqual(periodCollection);
      });
    });

    describe('comparePeriod', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePeriod(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePeriod(entity1, entity2);
        const compareResult2 = service.comparePeriod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePeriod(entity1, entity2);
        const compareResult2 = service.comparePeriod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePeriod(entity1, entity2);
        const compareResult2 = service.comparePeriod(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
