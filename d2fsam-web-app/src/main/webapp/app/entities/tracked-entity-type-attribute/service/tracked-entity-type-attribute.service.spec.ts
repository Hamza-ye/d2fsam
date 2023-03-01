import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-type-attribute.test-samples';

import { TrackedEntityTypeAttributeService, RestTrackedEntityTypeAttribute } from './tracked-entity-type-attribute.service';

const requireRestSample: RestTrackedEntityTypeAttribute = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityTypeAttribute Service', () => {
  let service: TrackedEntityTypeAttributeService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityTypeAttribute | ITrackedEntityTypeAttribute[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityTypeAttributeService);
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

    it('should create a TrackedEntityTypeAttribute', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityTypeAttribute = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityTypeAttribute).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityTypeAttribute', () => {
      const trackedEntityTypeAttribute = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityTypeAttribute).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityTypeAttribute', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityTypeAttribute', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityTypeAttribute', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityTypeAttributeToCollectionIfMissing', () => {
      it('should add a TrackedEntityTypeAttribute to an empty array', () => {
        const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing([], trackedEntityTypeAttribute);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityTypeAttribute);
      });

      it('should not add a TrackedEntityTypeAttribute to an array that contains it', () => {
        const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = sampleWithRequiredData;
        const trackedEntityTypeAttributeCollection: ITrackedEntityTypeAttribute[] = [
          {
            ...trackedEntityTypeAttribute,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing(
          trackedEntityTypeAttributeCollection,
          trackedEntityTypeAttribute
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityTypeAttribute to an array that doesn't contain it", () => {
        const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = sampleWithRequiredData;
        const trackedEntityTypeAttributeCollection: ITrackedEntityTypeAttribute[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing(
          trackedEntityTypeAttributeCollection,
          trackedEntityTypeAttribute
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityTypeAttribute);
      });

      it('should add only unique TrackedEntityTypeAttribute to an array', () => {
        const trackedEntityTypeAttributeArray: ITrackedEntityTypeAttribute[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const trackedEntityTypeAttributeCollection: ITrackedEntityTypeAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing(
          trackedEntityTypeAttributeCollection,
          ...trackedEntityTypeAttributeArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = sampleWithRequiredData;
        const trackedEntityTypeAttribute2: ITrackedEntityTypeAttribute = sampleWithPartialData;
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing(
          [],
          trackedEntityTypeAttribute,
          trackedEntityTypeAttribute2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityTypeAttribute);
        expect(expectedResult).toContain(trackedEntityTypeAttribute2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing([], null, trackedEntityTypeAttribute, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityTypeAttribute);
      });

      it('should return initial array if no TrackedEntityTypeAttribute is added', () => {
        const trackedEntityTypeAttributeCollection: ITrackedEntityTypeAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityTypeAttributeToCollectionIfMissing(trackedEntityTypeAttributeCollection, undefined, null);
        expect(expectedResult).toEqual(trackedEntityTypeAttributeCollection);
      });
    });

    describe('compareTrackedEntityTypeAttribute', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityTypeAttribute(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityTypeAttribute(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityTypeAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityTypeAttribute(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityTypeAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityTypeAttribute(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityTypeAttribute(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
