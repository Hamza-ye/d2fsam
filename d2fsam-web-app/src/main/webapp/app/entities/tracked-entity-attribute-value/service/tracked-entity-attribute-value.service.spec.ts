import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-attribute-value.test-samples';

import { TrackedEntityAttributeValueService, RestTrackedEntityAttributeValue } from './tracked-entity-attribute-value.service';

const requireRestSample: RestTrackedEntityAttributeValue = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityAttributeValue Service', () => {
  let service: TrackedEntityAttributeValueService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityAttributeValue | ITrackedEntityAttributeValue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityAttributeValueService);
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

    it('should create a TrackedEntityAttributeValue', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityAttributeValue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityAttributeValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityAttributeValue', () => {
      const trackedEntityAttributeValue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityAttributeValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityAttributeValue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityAttributeValue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityAttributeValue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityAttributeValueToCollectionIfMissing', () => {
      it('should add a TrackedEntityAttributeValue to an empty array', () => {
        const trackedEntityAttributeValue: ITrackedEntityAttributeValue = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing([], trackedEntityAttributeValue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityAttributeValue);
      });

      it('should not add a TrackedEntityAttributeValue to an array that contains it', () => {
        const trackedEntityAttributeValue: ITrackedEntityAttributeValue = sampleWithRequiredData;
        const trackedEntityAttributeValueCollection: ITrackedEntityAttributeValue[] = [
          {
            ...trackedEntityAttributeValue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing(
          trackedEntityAttributeValueCollection,
          trackedEntityAttributeValue
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityAttributeValue to an array that doesn't contain it", () => {
        const trackedEntityAttributeValue: ITrackedEntityAttributeValue = sampleWithRequiredData;
        const trackedEntityAttributeValueCollection: ITrackedEntityAttributeValue[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing(
          trackedEntityAttributeValueCollection,
          trackedEntityAttributeValue
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityAttributeValue);
      });

      it('should add only unique TrackedEntityAttributeValue to an array', () => {
        const trackedEntityAttributeValueArray: ITrackedEntityAttributeValue[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const trackedEntityAttributeValueCollection: ITrackedEntityAttributeValue[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing(
          trackedEntityAttributeValueCollection,
          ...trackedEntityAttributeValueArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityAttributeValue: ITrackedEntityAttributeValue = sampleWithRequiredData;
        const trackedEntityAttributeValue2: ITrackedEntityAttributeValue = sampleWithPartialData;
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing(
          [],
          trackedEntityAttributeValue,
          trackedEntityAttributeValue2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityAttributeValue);
        expect(expectedResult).toContain(trackedEntityAttributeValue2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityAttributeValue: ITrackedEntityAttributeValue = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing([], null, trackedEntityAttributeValue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityAttributeValue);
      });

      it('should return initial array if no TrackedEntityAttributeValue is added', () => {
        const trackedEntityAttributeValueCollection: ITrackedEntityAttributeValue[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityAttributeValueToCollectionIfMissing(
          trackedEntityAttributeValueCollection,
          undefined,
          null
        );
        expect(expectedResult).toEqual(trackedEntityAttributeValueCollection);
      });
    });

    describe('compareTrackedEntityAttributeValue', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityAttributeValue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityAttributeValue(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityAttributeValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityAttributeValue(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityAttributeValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityAttributeValue(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityAttributeValue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
