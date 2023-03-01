import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDataInputPeriod } from '../data-input-period.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../data-input-period.test-samples';

import { DataInputPeriodService, RestDataInputPeriod } from './data-input-period.service';

const requireRestSample: RestDataInputPeriod = {
  ...sampleWithRequiredData,
  openingDate: sampleWithRequiredData.openingDate?.format(DATE_FORMAT),
  closingDate: sampleWithRequiredData.closingDate?.format(DATE_FORMAT),
};

describe('DataInputPeriod Service', () => {
  let service: DataInputPeriodService;
  let httpMock: HttpTestingController;
  let expectedResult: IDataInputPeriod | IDataInputPeriod[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DataInputPeriodService);
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

    it('should create a DataInputPeriod', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const dataInputPeriod = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dataInputPeriod).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DataInputPeriod', () => {
      const dataInputPeriod = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dataInputPeriod).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DataInputPeriod', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DataInputPeriod', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DataInputPeriod', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDataInputPeriodToCollectionIfMissing', () => {
      it('should add a DataInputPeriod to an empty array', () => {
        const dataInputPeriod: IDataInputPeriod = sampleWithRequiredData;
        expectedResult = service.addDataInputPeriodToCollectionIfMissing([], dataInputPeriod);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dataInputPeriod);
      });

      it('should not add a DataInputPeriod to an array that contains it', () => {
        const dataInputPeriod: IDataInputPeriod = sampleWithRequiredData;
        const dataInputPeriodCollection: IDataInputPeriod[] = [
          {
            ...dataInputPeriod,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDataInputPeriodToCollectionIfMissing(dataInputPeriodCollection, dataInputPeriod);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DataInputPeriod to an array that doesn't contain it", () => {
        const dataInputPeriod: IDataInputPeriod = sampleWithRequiredData;
        const dataInputPeriodCollection: IDataInputPeriod[] = [sampleWithPartialData];
        expectedResult = service.addDataInputPeriodToCollectionIfMissing(dataInputPeriodCollection, dataInputPeriod);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataInputPeriod);
      });

      it('should add only unique DataInputPeriod to an array', () => {
        const dataInputPeriodArray: IDataInputPeriod[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dataInputPeriodCollection: IDataInputPeriod[] = [sampleWithRequiredData];
        expectedResult = service.addDataInputPeriodToCollectionIfMissing(dataInputPeriodCollection, ...dataInputPeriodArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dataInputPeriod: IDataInputPeriod = sampleWithRequiredData;
        const dataInputPeriod2: IDataInputPeriod = sampleWithPartialData;
        expectedResult = service.addDataInputPeriodToCollectionIfMissing([], dataInputPeriod, dataInputPeriod2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataInputPeriod);
        expect(expectedResult).toContain(dataInputPeriod2);
      });

      it('should accept null and undefined values', () => {
        const dataInputPeriod: IDataInputPeriod = sampleWithRequiredData;
        expectedResult = service.addDataInputPeriodToCollectionIfMissing([], null, dataInputPeriod, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dataInputPeriod);
      });

      it('should return initial array if no DataInputPeriod is added', () => {
        const dataInputPeriodCollection: IDataInputPeriod[] = [sampleWithRequiredData];
        expectedResult = service.addDataInputPeriodToCollectionIfMissing(dataInputPeriodCollection, undefined, null);
        expect(expectedResult).toEqual(dataInputPeriodCollection);
      });
    });

    describe('compareDataInputPeriod', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDataInputPeriod(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDataInputPeriod(entity1, entity2);
        const compareResult2 = service.compareDataInputPeriod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDataInputPeriod(entity1, entity2);
        const compareResult2 = service.compareDataInputPeriod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDataInputPeriod(entity1, entity2);
        const compareResult2 = service.compareDataInputPeriod(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
