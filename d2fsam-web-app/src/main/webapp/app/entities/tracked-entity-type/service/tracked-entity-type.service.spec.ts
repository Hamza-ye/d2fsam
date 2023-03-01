import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityType } from '../tracked-entity-type.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tracked-entity-type.test-samples';

import { TrackedEntityTypeService, RestTrackedEntityType } from './tracked-entity-type.service';

const requireRestSample: RestTrackedEntityType = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityType Service', () => {
  let service: TrackedEntityTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityType | ITrackedEntityType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityTypeService);
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

    it('should create a TrackedEntityType', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityType', () => {
      const trackedEntityType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityTypeToCollectionIfMissing', () => {
      it('should add a TrackedEntityType to an empty array', () => {
        const trackedEntityType: ITrackedEntityType = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing([], trackedEntityType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityType);
      });

      it('should not add a TrackedEntityType to an array that contains it', () => {
        const trackedEntityType: ITrackedEntityType = sampleWithRequiredData;
        const trackedEntityTypeCollection: ITrackedEntityType[] = [
          {
            ...trackedEntityType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing(trackedEntityTypeCollection, trackedEntityType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityType to an array that doesn't contain it", () => {
        const trackedEntityType: ITrackedEntityType = sampleWithRequiredData;
        const trackedEntityTypeCollection: ITrackedEntityType[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing(trackedEntityTypeCollection, trackedEntityType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityType);
      });

      it('should add only unique TrackedEntityType to an array', () => {
        const trackedEntityTypeArray: ITrackedEntityType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const trackedEntityTypeCollection: ITrackedEntityType[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing(trackedEntityTypeCollection, ...trackedEntityTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityType: ITrackedEntityType = sampleWithRequiredData;
        const trackedEntityType2: ITrackedEntityType = sampleWithPartialData;
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing([], trackedEntityType, trackedEntityType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityType);
        expect(expectedResult).toContain(trackedEntityType2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityType: ITrackedEntityType = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing([], null, trackedEntityType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityType);
      });

      it('should return initial array if no TrackedEntityType is added', () => {
        const trackedEntityTypeCollection: ITrackedEntityType[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityTypeToCollectionIfMissing(trackedEntityTypeCollection, undefined, null);
        expect(expectedResult).toEqual(trackedEntityTypeCollection);
      });
    });

    describe('compareTrackedEntityType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityType(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityType(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityType(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
