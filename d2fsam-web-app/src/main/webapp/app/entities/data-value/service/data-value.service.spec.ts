import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDataValue } from '../data-value.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../data-value.test-samples';

import { DataValueService, RestDataValue } from './data-value.service';

const requireRestSample: RestDataValue = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('DataValue Service', () => {
  let service: DataValueService;
  let httpMock: HttpTestingController;
  let expectedResult: IDataValue | IDataValue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DataValueService);
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

    it('should create a DataValue', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const dataValue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dataValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DataValue', () => {
      const dataValue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dataValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DataValue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DataValue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DataValue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDataValueToCollectionIfMissing', () => {
      it('should add a DataValue to an empty array', () => {
        const dataValue: IDataValue = sampleWithRequiredData;
        expectedResult = service.addDataValueToCollectionIfMissing([], dataValue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dataValue);
      });

      it('should not add a DataValue to an array that contains it', () => {
        const dataValue: IDataValue = sampleWithRequiredData;
        const dataValueCollection: IDataValue[] = [
          {
            ...dataValue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDataValueToCollectionIfMissing(dataValueCollection, dataValue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DataValue to an array that doesn't contain it", () => {
        const dataValue: IDataValue = sampleWithRequiredData;
        const dataValueCollection: IDataValue[] = [sampleWithPartialData];
        expectedResult = service.addDataValueToCollectionIfMissing(dataValueCollection, dataValue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataValue);
      });

      it('should add only unique DataValue to an array', () => {
        const dataValueArray: IDataValue[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dataValueCollection: IDataValue[] = [sampleWithRequiredData];
        expectedResult = service.addDataValueToCollectionIfMissing(dataValueCollection, ...dataValueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dataValue: IDataValue = sampleWithRequiredData;
        const dataValue2: IDataValue = sampleWithPartialData;
        expectedResult = service.addDataValueToCollectionIfMissing([], dataValue, dataValue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataValue);
        expect(expectedResult).toContain(dataValue2);
      });

      it('should accept null and undefined values', () => {
        const dataValue: IDataValue = sampleWithRequiredData;
        expectedResult = service.addDataValueToCollectionIfMissing([], null, dataValue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dataValue);
      });

      it('should return initial array if no DataValue is added', () => {
        const dataValueCollection: IDataValue[] = [sampleWithRequiredData];
        expectedResult = service.addDataValueToCollectionIfMissing(dataValueCollection, undefined, null);
        expect(expectedResult).toEqual(dataValueCollection);
      });
    });

    describe('compareDataValue', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDataValue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDataValue(entity1, entity2);
        const compareResult2 = service.compareDataValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDataValue(entity1, entity2);
        const compareResult2 = service.compareDataValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDataValue(entity1, entity2);
        const compareResult2 = service.compareDataValue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
